package com.portafolio.subastas.web.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        String message,
        String error,
        int status,
        LocalDateTime timestamp
) {
}
