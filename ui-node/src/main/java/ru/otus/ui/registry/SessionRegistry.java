package ru.otus.ui.registry;

import lombok.*;
import org.slf4j.Logger;
import ru.otus.common.cdi.event.Incoming;
import ru.otus.common.cdi.event.Outcoming;
import ru.otus.common.cdi.event.Transport;
import ru.otus.common.protocol.Key;
import ru.otus.common.protocol.Message;
import ru.otus.common.protocol.Type;
import ru.otus.transport.api.TransportState;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class SessionRegistry {

    private final AtomicReference<TransportState> transportState = new AtomicReference<>(TransportState.STOPPED);
    private final ConcurrentMap<String, AuthenticatedSession> sessions = new ConcurrentHashMap<>();

    @Inject
    @Outcoming
    private Event<Message> messageEvent;
    @Inject
    private Logger logger;

    public void onMessage(@ObservesAsync @Incoming Message message) {
        switch (message.getType()) {
            case AUTHENTICATION:
                processAuthentication(message);
                break;
            case DATA:
                broadcast(message);
                break;
        }
    }

    public void onTransportEvent(@ObservesAsync @Transport TransportState state) {
        switch (state) {
            case STARTED:
                transportState.compareAndSet(TransportState.STOPPED, state);
                break;
            case STOPPED:
                transportState.compareAndSet(TransportState.STARTED, state);
                break;
            default:
                logger.error("Illegal transport state: {}", state);
        }
    }

    public void register(Session s) {
        sessions.put(s.getId(), new AuthenticatedSession(s));
    }

    public void deregister(Session s) {
        sessions.remove(s.getId());
    }

    public void subscribe(Session s, Message m) throws IOException, EncodeException {
        if (!isReady()) {
            s.getBasicRemote().sendObject(new Message(Type.ERROR));
            return;
        }

        if (Type.AUTHENTICATION == m.getType()) {
            messageEvent.fireAsync(m.put(Key.SESSION_ID, s.getId()));
        }
    }

    private void processAuthentication(Message message) {
        String id = (String) message.get(Key.SESSION_ID);
        boolean authenticated = (Boolean) message.getOrDefault(Key.AUTHENTICATED, false);
        if (Objects.nonNull(id) && sessions.containsKey(id)) {
            AuthenticatedSession s = sessions.get(id);
            send(s.session, message);
            s.authenticated = authenticated;
        }
    }

    private boolean isReady() {
        return TransportState.STARTED == transportState.get();
    }


    private void broadcast(Message message) {
        sessions.values().parallelStream().filter(s -> s.authenticated).forEach(s -> send(s.session, message));
    }

    private void send(Session session, Message message) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendObject(message);
                logger.debug("Message successfully send into session {}", session.getId());
            }
        } catch (Exception e) {
            logger.warn("Can't send message into session: " + session.getId(), e);
        }
    }

    @RequiredArgsConstructor
    private static class AuthenticatedSession {
        private final Session session;
        private boolean authenticated;
    }
}
