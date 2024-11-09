package ru.mdm.files.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends ResponseStatusException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
