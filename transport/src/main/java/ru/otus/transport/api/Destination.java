package ru.otus.transport.api;

import java.io.Serializable;

public interface Destination<M extends Serializable> extends AutoCloseable {

    void put(M message);

}
