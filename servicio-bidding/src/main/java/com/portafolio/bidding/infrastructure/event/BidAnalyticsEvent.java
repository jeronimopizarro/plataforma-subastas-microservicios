package com.portafolio.bidding.infrastructure.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BidAnalyticsEvent(
        Long auctionId,
        Long bidderId,
        BigDecimal amount,
        LocalDateTime timestamp,
        String eventType
) {}