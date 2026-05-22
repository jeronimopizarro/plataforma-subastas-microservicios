package com.portafolio.bidding.web.exception;

import com.portafolio.bidding.domain.exception.DomainException;
import com.portafolio.bidding.web.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorResponse> handleDomainException(DomainException ex) {

        // Mapeamos nuestro código interno a un código HTTP real
        HttpStatus status = switch (ex.getErrorCode()) {
            case SERVICE_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                ex.getErrorCode().name(),
                status.value()
        );

        return ResponseEntity.status(status).body(response);
    }
}