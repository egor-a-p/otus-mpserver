package ru.otus.transport.api;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class TransportProcessor<M extends Serializable> {
    private static final int POOL_SIZE = 2;

    private final Connection<M> connection;
    private final Source<M> source;
    private final Destination<M> destination;

    private final List<Runnable> onShutdown;

    private ExecutorService executorService;

    public TransportProcessor(Connection<M> connection, Source<M> source, Destination<M> destination) {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(source);
        Objects.requireNonNull(destination);
        this.connection = connection;
        this.source = source;
        this.destination = destination;
        this.onShutdown = new CopyOnWriteArrayList<>();
    }

    public void start(Runnable... onShutdown) throws TransportException {
        if (connection.isOpen()) {
            log.debug("Start transport...");
            executorService = Executors.newFixedThreadPool(POOL_SIZE);
            Collections.addAll(this.onShutdown, onShutdown);
            executorService.submit(this::processSend);
            executorService.submit(this::processReceive);
        } else {
            log.error("Can't start transport, connection closed!");
            throw new TransportException("Can't start transport process: connection is closed!");
        }
    }

    private void processReceive() {
        try (Source<M> connectionSource = connection.source()) {
            while (connection.isOpen()) {
                destination.put(connectionSource.take());
            }
        } catch (Exception e) {
            log.error("Receive error, transport immediately shutdown!", e);
        } finally {
            shutdown();
        }
    }

    private void processSend() {
        try (Destination<M> connectionDestination = connection.destination()) {
            while (connection.isOpen()) {
                connectionDestination.put(source.take());
            }
        } catch (Exception e) {
            log.error("Send error, transport immediately shutdown!", e);
        } finally {
            shutdown();
        }
    }

    public void shutdown() {
        log.debug("Shutdown transport...");
        if (Objects.nonNull(executorService)) {
            executorService.shutdown();
        }
        onShutdown.forEach(Runnable::run);
        onShutdown.clear();
    }
}
