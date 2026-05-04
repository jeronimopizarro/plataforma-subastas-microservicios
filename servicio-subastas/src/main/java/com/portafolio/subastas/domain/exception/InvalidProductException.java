package com.portafolio.subastas.domain.exception;

public class InvalidProductException extends DomainException {
    public InvalidProductException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}
