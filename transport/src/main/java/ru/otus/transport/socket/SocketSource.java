package ru.otus.transport.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.otus.transport.api.Source;
import ru.otus.transport.api.SourceException;

class SocketSource<M extends Serializable> implements Source<M> {

    private final Lock lock = new ReentrantLock(true);
    private ObjectInputStream in;

    SocketSource(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    @SuppressWarnings("unchecked")
    public M take() throws SourceException {
        lock.lock();
        try {
            return (M) in.readObject();
        } catch (Exception e) {
            throw new SourceException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws SourceException {
        lock.lock();
        try {
            if (Objects.nonNull(in)) {
                in.close();
            }
        } catch (IOException e) {
            throw new SourceException(e);
        } finally {
            in = null;
            lock.unlock();
        }
    }

    @Override
    public void clear() {
    }
}
