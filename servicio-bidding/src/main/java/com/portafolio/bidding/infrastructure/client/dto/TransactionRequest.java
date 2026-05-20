package com.portafolio.bidding.infrastructure.client.dto;

import java.math.BigDecimal;

public record TransactionRequest(
        BigDecimal amount,
        String reference
) {}