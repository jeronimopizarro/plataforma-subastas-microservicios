package com.portafolio.bidding.infrastructure.client.dto;

import java.math.BigDecimal;

public record UpdateBidRequest(
        Long winnerId,
        BigDecimal amount
) {}