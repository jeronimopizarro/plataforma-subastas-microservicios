package com.portafolio.subastas.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateAuctionCommand(
        Long productId,
        BigDecimal startingPrice,
        LocalDateTime startTime,
        LocalDateTime endTime
) {}
