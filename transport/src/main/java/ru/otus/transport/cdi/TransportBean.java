package ru.otus.transport.cdi;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import ru.otus.common.cdi.Startup;
import ru.otus.common.protocol.Message;
import ru.otus.transport.api.Connection;
import ru.otus.transport.api.TransportException;
import ru.otus.transport.api.TransportProcessor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@Startup
@ApplicationScoped
public class TransportBean {
    @Inject
    private Connection<Message> connection;
    @Inject
    private MessageEventDestination destination;
    @Inject
    private MessageEventSource source;
    @Inject
    private Logger logger;

    private TransportProcessor<Message> transportProcessor;

    @PostConstruct
    public void init() {
        try {
            logger.debug("Init transport...");
            transportProcessor = new TransportProcessor<>(connection, source, destination);
            transportProcessor.start(this::close);
        } catch (Exception e) {
            logger.error("Can't start transport!", e);
            close();
        }
    }

    @SneakyThrows
    private void close() {
        source.close();
        destination.close();
        connection.close();
    }

    @PreDestroy
    public void destroy() {
        logger.debug("Shutdown transport...");
        transportProcessor.shutdown();
    }
}
