package ru.otus.transport.cdi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.slf4j.Logger;

import ru.otus.common.cdi.event.Outcoming;
import ru.otus.common.protocol.Message;
import ru.otus.transport.api.Source;
import ru.otus.transport.api.SourceException;

public class MessageEventSource implements Source<Message> {
    @Inject
    private Logger logger;
    private final BlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    public void onMessage(@ObservesAsync @Outcoming Message message) {
        try {
            messages.put(message);
            logger.debug("Add message to queue: {}", message);
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
    public void clear() {
        messages.clear();
        logger.debug("Clear queue.");
    }

    @Override
    public void close() throws Exception {
        clear();
    }
}
