package com.portafolio.subastas.web.dto;

import com.portafolio.subastas.domain.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AuctionResponse(
        Long id,
        Long productId,
        Long sellerId,
        BigDecimal startingPrice,
        BigDecimal currentHighestBid,
        LocalDateTime startTime,
        LocalDateTime endTime,
        AuctionStatus status,
        Long winnerId
) {
}
