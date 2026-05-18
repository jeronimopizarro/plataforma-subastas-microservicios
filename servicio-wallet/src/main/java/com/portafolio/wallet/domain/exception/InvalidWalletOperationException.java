package com.portafolio.wallet.domain.exception;

public class InvalidWalletOperationException extends DomainException {

    public InvalidWalletOperationException(String message) {
        super(message, ErrorCode.INVALID_WALLET_OPERATION);
    }
}
