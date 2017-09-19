package ru.otus.transport.cdi;

import org.slf4j.Logger;
import ru.otus.common.cdi.event.Outcoming;
import ru.otus.common.protocol.Message;
import ru.otus.transport.api.Source;
import ru.otus.transport.api.SourceException;
import ru.otus.transport.api.TransportException;


import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageEventSource implements Source<Message> {
    @Inject
    private Logger logger;
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    public void onMessage(@ObservesAsync @Outcoming Message message) throws TransportException {
        try {
            if (!isClosed.get()) {
                messages.put(message);
                logger.debug("Add message to queue: {}", message);
            } else {
                throw new TransportException("MessageEventSource is closed!");
            }
        } catch (InterruptedException e) {
            logger.error("OnMessage source error: ", e);
        }
    }

    @Override
    public Message take() {
        try {
            return messages.take();
        } catch (InterruptedException e) {
            logger.error("Source take error: ", e);
            throw new SourceException(e);
        }
    }

    @Override
    public void close() throws Exception {
        isClosed.compareAndSet(false, true);
        messages.clear();
        logger.debug("Clear queue.");
    }
}
