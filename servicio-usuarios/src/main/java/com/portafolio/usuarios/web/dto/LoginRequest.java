package com.portafolio.usuarios.web.dto;

public record LoginRequest(
        String email,
        String password
) {}