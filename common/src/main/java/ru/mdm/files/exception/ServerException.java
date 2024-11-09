package ru.mdm.files.exception;

public class ServerException extends ResponseStatusException {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
