package com.portafolio.usuarios.application.dto;

public record AuthenticateUserCommand(
        String email,
        String password
) {}