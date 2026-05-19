package com.portafolio.subastas.web.dto;

import java.math.BigDecimal;

public record UpdateBidRequest(
        Long winnerId,
        BigDecimal amount
) {}