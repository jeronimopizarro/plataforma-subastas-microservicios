package com.portafolio.gateway.web.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String username
) {}