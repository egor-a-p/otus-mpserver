package ru.otus.transport.cdi.producer;

import ru.otus.common.protocol.Message;
import ru.otus.transport.api.Connection;
import ru.otus.transport.api.TransportException;
import ru.otus.transport.socket.SocketConnection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.net.Socket;

import static ru.otus.common.Configuration.*;

public class MessageConnectionProducer {

    @Produces @ApplicationScoped
    public Connection<Message> getConnection() throws Exception {
        Socket socket = new Socket(masterHost(), masterPort());
        Connection<Message> connection = new SocketConnection<>(socket);
        connection.destination().put(new Message(Message.Type.HANDSHAKE).put(Message.Key.NODE_UUID, uuid()));
        Message message = connection.source().take();
        if (Message.Type.HANDSHAKE == message.getType() && masterUuid().equals(message.get(Message.Key.NODE_UUID))) {
            return connection;
        } else {
            throw new TransportException("Handshake failed");
        }
    }
}
