package com.portafolio.subastas.web.exception;

import com.portafolio.subastas.domain.exception.DomainException;
import com.portafolio.subastas.domain.exception.ErrorCode;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.web.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(DomainException ex) {

        HttpStatus status = mapErrorCodeToHttpStatus(ex.getErrorCode());

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                status.getReasonPhrase(),
                status.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        System.err.println("Error inesperado: " + ex.getMessage());
        ex.printStackTrace();

        ApiErrorResponse response = new ApiErrorResponse(
                "Ocurrió un error interno en el servidor.",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;       // 404
            case CONFLICT -> HttpStatus.CONFLICT;         // 409
            default -> HttpStatus.BAD_REQUEST;            // 400 por defecto para reglas rotas
        };
    }
}
