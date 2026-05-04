package com.portafolio.subastas.domain.exception;

public class InvalidAuctionException extends DomainException {
    public InvalidAuctionException(String message) {
        super(message, ErrorCode.BAD_REQUEST);
    }
}
