package ru.otus.transport.api;


public class DestinationException extends RuntimeException {
    public DestinationException() {
    }

    public DestinationException(String message) {
        super(message);
    }

    public DestinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DestinationException(Throwable cause) {
        super(cause);
    }
}
