package ru.otus.ui.endpoint;

import org.slf4j.Logger;
import ru.otus.common.protocol.Message;
import ru.otus.ui.registry.SessionRegistry;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/cache", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class CacheEndpoint {
    @Inject
    private SessionRegistry registry;

    @Inject
    private Logger logger;

    @OnOpen
    public void onOpen(final Session session) {
        logger.debug("Open new session {}", session.getId());
        registry.add(session);
    }

    @OnMessage
    public void onMessage(final Session session, final Message message) throws IOException, EncodeException {
        logger.debug("Receive message {} from session {}", message, session.getId());
        if (Message.Type.AUTHENTICATION_REQUEST == message.getType()) {
            registry.requestAccess(session, message);
        }
    }

    @OnClose
    public void onClose(final Session session) throws IOException {
        registry.remove(session);
    }

    @OnError
    public void onError(final Session session, Throwable t) throws IOException {
        logger.error("Error in session " + session.getId(), t);
        registry.remove(session);
    }
}
