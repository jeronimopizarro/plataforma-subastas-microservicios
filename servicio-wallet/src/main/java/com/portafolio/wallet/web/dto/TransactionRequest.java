package com.portafolio.wallet.web.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        String reference
) {
}