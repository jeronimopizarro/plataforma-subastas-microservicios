package com.portafolio.wallet.web.exception;

import com.portafolio.wallet.domain.exception.DomainException;
import com.portafolio.wallet.domain.exception.ErrorCode;
import com.portafolio.wallet.web.dto.ApiErrorResponse;
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
        System.err.println("Error inesperado en Wallet: " + ex.getMessage());
        ex.printStackTrace();

        ApiErrorResponse response = new ApiErrorResponse(
                "Ocurrió un error interno en el servidor de billeteras.",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private HttpStatus mapErrorCodeToHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case WALLET_NOT_FOUND -> HttpStatus.NOT_FOUND;       // 404
            case INSUFFICIENT_FUNDS, INVALID_WALLET_OPERATION -> HttpStatus.BAD_REQUEST; // 400
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}