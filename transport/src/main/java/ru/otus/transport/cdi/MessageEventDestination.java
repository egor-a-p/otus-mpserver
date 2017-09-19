package ru.otus.transport.cdi;

import org.slf4j.Logger;
import ru.otus.common.cdi.event.Incoming;
import ru.otus.common.protocol.Message;
import ru.otus.transport.api.Destination;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class MessageEventDestination implements Destination<Message> {
    @Inject @Incoming
    private Event<Message> messageEvent;
    @Inject
    private Logger logger;

    @Override
    public void put(Message message) {
        messageEvent.fireAsync(message);
        logger.debug("Fire new message: {}", message);
    }

    @Override
    public void close() throws Exception {
    }
}
