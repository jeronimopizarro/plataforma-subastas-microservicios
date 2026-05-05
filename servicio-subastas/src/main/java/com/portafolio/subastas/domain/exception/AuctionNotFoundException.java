package com.portafolio.subastas.domain.exception;

public class AuctionNotFoundException extends DomainException {
    public AuctionNotFoundException(Long id) {
        super("La subasta con ID " + id + " no existe.", ErrorCode.NOT_FOUND);
    }
}
