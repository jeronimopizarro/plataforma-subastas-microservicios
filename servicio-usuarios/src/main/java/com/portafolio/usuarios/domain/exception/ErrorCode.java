package com.portafolio.usuarios.domain.exception;

public enum ErrorCode {
    USER_NOT_FOUND("El usuario solicitado no existe"),
    INVALID_CREDENTIALS("Credenciales inválidas"),
    USER_ALREADY_EXISTS("El correo electrónico ya está en uso");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}