package com.portafolio.bidding.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidResponse(
        Long id,
        Long auctionId,
        Long bidderId,
        String bidderEmail,
        BigDecimal amount,
        LocalDateTime timestamp) {
}
