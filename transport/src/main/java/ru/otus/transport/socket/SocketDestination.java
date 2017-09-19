package ru.otus.transport.socket;

import ru.otus.transport.api.Destination;
import ru.otus.transport.api.DestinationException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class SocketDestination<M extends Serializable> implements Destination<M> {

    private final Lock lock = new ReentrantLock(true);
    private ObjectOutputStream out;

    SocketDestination(ObjectOutputStream out) {
        this.out = out;
    }

    @Override
    public void put(M message) {
        lock.lock();
        try {
            out.writeObject(message);
        } catch (Exception e) {
            throw new DestinationException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws DestinationException {
        lock.lock();
        try {
            if (Objects.nonNull(out)) {
                out.close();
            }
        } catch (IOException e) {
            throw new DestinationException(e);
        } finally {
            out = null;
            lock.unlock();
        }
    }
}
