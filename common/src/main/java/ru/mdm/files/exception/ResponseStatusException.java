package ru.mdm.files.exception;

import lombok.Getter;

@Getter
public abstract class ResponseStatusException extends RuntimeException {

    private final ErrorCode errorCode;

    protected ResponseStatusException(String message) {
        super(message);
        errorCode = null;
    }

    protected ResponseStatusException(String message, Throwable cause) {
        super(message, cause);
        errorCode = null;
    }

    protected ResponseStatusException(ErrorCode errorCode) {
        super(errorCode.getText());
        this.errorCode = errorCode;
    }
}
