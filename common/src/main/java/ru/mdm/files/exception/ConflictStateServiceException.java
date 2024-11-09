package ru.mdm.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictStateServiceException extends ResponseStatusException {
    public ConflictStateServiceException(String message) {
        super(message);
    }

    public ConflictStateServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConflictStateServiceException(ErrorCode errorCode) {
        super(errorCode);
    }
}
