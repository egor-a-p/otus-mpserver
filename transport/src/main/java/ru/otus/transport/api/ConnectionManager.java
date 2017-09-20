package ru.otus.transport.api;

import java.io.Serializable;

public interface ConnectionManager<M extends Serializable> {
	Connection<M> getConnection() throws Exception;
}
