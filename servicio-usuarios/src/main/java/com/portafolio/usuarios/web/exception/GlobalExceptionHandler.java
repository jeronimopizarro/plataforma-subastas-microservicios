package com.portafolio.usuarios.web.exception;

import com.portafolio.usuarios.domain.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, String>> handleDomainException(DomainException ex) {

        HttpStatus status = switch (ex.getErrorCode()) {
            case INVALID_CREDENTIALS -> HttpStatus.UNAUTHORIZED; // 401
            case USER_NOT_FOUND -> HttpStatus.NOT_FOUND;         // 404
            case USER_ALREADY_EXISTS -> HttpStatus.CONFLICT;     // 409
            default -> HttpStatus.BAD_REQUEST;                   // 400
        };

        return ResponseEntity.status(status).body(Map.of("error", ex.getMessage()));
    }
}