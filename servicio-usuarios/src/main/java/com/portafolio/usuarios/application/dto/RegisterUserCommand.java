package com.portafolio.usuarios.application.dto;

public record RegisterUserCommand(
        String email,
        String password,
        String role
) {}