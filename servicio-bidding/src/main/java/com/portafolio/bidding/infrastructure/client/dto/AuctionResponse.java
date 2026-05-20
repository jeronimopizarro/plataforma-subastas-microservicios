package com.portafolio.bidding.infrastructure.client.dto;

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
        String status,
        Long winnerId
) {}