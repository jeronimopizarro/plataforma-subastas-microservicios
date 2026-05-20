package com.portafolio.bidding.application.port;

import java.math.BigDecimal;

public interface BidEventPublisher {
    void publishNewBid(Long auctionId, Long bidderId, BigDecimal amount);
}