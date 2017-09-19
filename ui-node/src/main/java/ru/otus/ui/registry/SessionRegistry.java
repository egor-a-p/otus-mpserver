package ru.otus.ui.registry;

import org.slf4j.Logger;
import ru.otus.common.cdi.event.Incoming;
import ru.otus.common.cdi.event.Outcoming;
import ru.otus.common.protocol.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.websocket.Session;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@ApplicationScoped
public class SessionRegistry {
    private final ConcurrentMap<String, Session> sessions = new ConcurrentHashMap<>();
    private final List<Session> subscribers = new CopyOnWriteArrayList<>();

    @Inject
    @Outcoming
    private Event<Message> messageEvent;
    @Inject
    private Logger logger;

    public void onMessage(@ObservesAsync @Incoming Message message) {
        switch (message.getType()) {
            case AUTHENTICATION_RESPONSE:
                Session session = sessions.get(String.valueOf(message.get(Message.Key.SESSION_ID)));
                if (sendMessage(session, message)) {
                    subscribers.add(session);
                }
                break;
            case CACHE_STATE:
                broadcast(message);
                break;
        }
    }

    private void broadcast(Message message) {
        subscribers.removeIf(s -> sendMessage(s, message));
    }

    private boolean sendMessage(Session session, Message message) {
        try {
            Objects.requireNonNull(session);
            Objects.requireNonNull(message);

            if (!session.isOpen()) {
                throw new IllegalStateException("Session is closed!");
            }

            session.getBasicRemote().sendObject(message);
            logger.debug("Message successfully send into session {}", session.getId());
            return true;
        } catch (Exception e) {
            logger.warn("Can't send message into session: " + session.getId(), e);
            sessions.remove(session.getId());
            return false;
        }
    }

    public void add(Session session) {
        if (session.isOpen()) {
            sessions.put(session.getId(), session);
        }
    }

    public void remove(Session session) {
        sessions.remove(session.getId());
    }

    public void requestAccess(Session session, Message message) {
        if (sessions.containsKey(session.getId()) && session.isOpen()) {
            messageEvent.fireAsync(message.put(Message.Key.SESSION_ID, session.getId()));
        }
    }
}
