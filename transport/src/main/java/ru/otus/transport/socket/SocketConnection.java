package ru.otus.transport.socket;

import ru.otus.transport.api.Connection;
import ru.otus.transport.api.Destination;
import ru.otus.transport.api.Source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Objects;

public final class SocketConnection<M extends Serializable> implements Connection<M> {

    private final Socket socket;
    private final SocketSource<M> source;
    private final SocketDestination<M> destination;

    public SocketConnection(Socket socket) throws IOException {
        if (Objects.nonNull(socket) && socket.isConnected()) {
            this.socket = socket;
            this.destination = new SocketDestination<>(new ObjectOutputStream(socket.getOutputStream()));
            this.source = new SocketSource<>(new ObjectInputStream(socket.getInputStream()));
        } else {
            throw new ConnectException("Can't create connection: socket is null or is not connected!");
        }
    }

    @Override
    public boolean isOpen() {
        return socket.isConnected();
    }

    @Override
    public Source<M> source() {
        return source;
    }

    @Override
    public Destination<M> destination() {
        return destination;
    }

    @Override
    public void close() throws Exception {
        source.close();
        destination.close();
        socket.close();
    }
}
