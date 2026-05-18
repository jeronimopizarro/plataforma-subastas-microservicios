package com.portafolio.wallet.domain.exception;

public class WalletNotFoundException extends DomainException {

    public WalletNotFoundException(String message) {
        super(message, ErrorCode.WALLET_NOT_FOUND);
    }
}
