package com.portafolio.usuarios.web.dto;

public record AuthResponse(
        String accessToken,
        String email,
        Long userId
) {}