package ru.mdm.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestServerException extends ResponseStatusException {

    public BadRequestServerException(String message) {
        super(message);
    }

    public BadRequestServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
