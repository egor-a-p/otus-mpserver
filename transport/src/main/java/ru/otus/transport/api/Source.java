package ru.otus.transport.api;

import java.io.Serializable;

public interface Source<M extends Serializable> extends AutoCloseable {

    M take();

}
