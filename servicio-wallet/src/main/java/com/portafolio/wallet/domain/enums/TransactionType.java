package com.portafolio.wallet.domain.enums;

public enum TransactionType {
    DEPOSIT,    // Ingreso de dinero a la cuenta
    HOLD,       // Dinero congelado por una puja activa
    RELEASE,    // Dinero devuelto porque alguien superó la puja
    COMMIT      // Dinero descontado definitivamente por ganar la subasta
}
