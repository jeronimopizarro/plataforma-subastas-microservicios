package com.portafolio.wallet.web.dto;

import java.math.BigDecimal;

public record WalletResponse(
        Long id,
        Long userId,
        BigDecimal availableBalance,
        BigDecimal heldFunds
) {
}
