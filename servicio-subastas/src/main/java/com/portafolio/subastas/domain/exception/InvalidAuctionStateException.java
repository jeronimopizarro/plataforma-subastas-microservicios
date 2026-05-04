package com.portafolio.subastas.domain.exception;

public class InvalidAuctionStateException extends DomainException {
    public InvalidAuctionStateException(String message) {
        super(message, ErrorCode.CONFLICT);
    }
}
