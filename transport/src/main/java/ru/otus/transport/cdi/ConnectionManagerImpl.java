package ru.otus.transport.cdi;

import java.net.Socket;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;

import ru.otus.common.protocol.Key;
import ru.otus.common.protocol.Message;
import ru.otus.common.protocol.Type;
import ru.otus.transport.api.Connection;
import ru.otus.transport.api.ConnectionManager;
import ru.otus.transport.api.TransportException;
import ru.otus.transport.socket.SocketConnection;

import static ru.otus.common.Configuration.masterHost;
import static ru.otus.common.Configuration.masterPort;
import static ru.otus.common.Configuration.masterUuid;
import static ru.otus.common.Configuration.uuid;

public class ConnectionManagerImpl implements ConnectionManager<Message> {

	private Connection<Message> connection;

	public Connection<Message> getConnection() throws Exception {
		if (Objects.isNull(connection) || !connection.isOpen()) {
			Socket socket = new Socket(masterHost(), masterPort());
			Connection<Message> connection = new SocketConnection<>(socket);
			connection.destination().put(new Message(Type.HANDSHAKE).put(Key.NODE_UUID, uuid()));
			Message message = connection.source().take();
			if (Type.HANDSHAKE == message.getType() && masterUuid().equals(message.get(Key.NODE_UUID))) {
				return (this.connection = connection);
			} else {
				throw new TransportException("Handshake failed!");
			}
		} else {
			return connection;
		}
	}
}
