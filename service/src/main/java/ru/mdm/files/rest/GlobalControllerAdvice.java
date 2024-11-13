package ru.mdm.files.rest;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.mdm.files.exception.BadRequestServerException;

import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    private static final String VIOLATION_ERROR_MSG_PREFIX = "Параметры не соответствуют ожидаемым: ";

    private final ErrorWebExceptionHandler errorWebExceptionHandler;

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<Void> handleConstraintViolation(ServerWebExchange exchange, ConstraintViolationException e) {
        var message = VIOLATION_ERROR_MSG_PREFIX + e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        var exception = new BadRequestServerException(message);
        return errorWebExceptionHandler.handle(exchange, exception);
    }

    @ExceptionHandler(MismatchedInputException.class)
    public Mono<Void> handleNotReadableMessage(ServerWebExchange exchange, MismatchedInputException e) {
        return errorWebExceptionHandler.handle(exchange, new BadRequestServerException(e.getMessage() + "1"));
    }
}
