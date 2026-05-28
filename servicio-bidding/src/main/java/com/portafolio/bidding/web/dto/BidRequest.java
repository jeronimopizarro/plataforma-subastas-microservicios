package com.portafolio.bidding.web.dto;

import java.math.BigDecimal;

public record BidRequest(
        Long auctionId,
        BigDecimal amount
) {
}