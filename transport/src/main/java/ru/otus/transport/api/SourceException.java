package ru.otus.transport.api;

public class SourceException extends RuntimeException {
    public SourceException() {
    }

    public SourceException(String message) {
        super(message);
    }

    public SourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceException(Throwable cause) {
        super(cause);
    }
}
