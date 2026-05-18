package com.portafolio.wallet.domain.exception;

public class InsufficientFundsException extends DomainException {

    public InsufficientFundsException(String message) {
        super(message, ErrorCode.INSUFFICIENT_FUNDS);
    }
}
