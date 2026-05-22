package com.portafolio.bidding.web.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String message,
        String errorCode,
        int status,
        LocalDateTime timestamp
) {
    public ApiErrorResponse(String message, String errorCode, int status) {
        this(message, errorCode, status, LocalDateTime.now());
    }
}