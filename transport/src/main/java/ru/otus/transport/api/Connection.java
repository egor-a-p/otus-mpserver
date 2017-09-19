package ru.otus.transport.api;


import java.io.Serializable;


public interface Connection<M extends Serializable> extends AutoCloseable {

    Source<M> source();

    Destination<M> destination();

    boolean isOpen();

}
