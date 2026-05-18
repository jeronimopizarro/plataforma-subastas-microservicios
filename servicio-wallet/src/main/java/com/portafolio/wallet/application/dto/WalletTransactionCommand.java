package com.portafolio.wallet.application.dto;

import java.math.BigDecimal;

public record WalletTransactionCommand(
        Long userId,
        BigDecimal amount,
        String reference
) {
}
