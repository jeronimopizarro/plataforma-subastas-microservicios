package com.portafolio.subastas.domain.exception;

public class ProductNotFoundException extends DomainException {
    public ProductNotFoundException(Long productId) {
        super("El producto con ID " + productId + " no existe.", ErrorCode.NOT_FOUND);
    }
}
