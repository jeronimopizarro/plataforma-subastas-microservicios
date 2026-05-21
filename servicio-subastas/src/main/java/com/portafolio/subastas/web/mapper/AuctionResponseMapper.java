package com.portafolio.subastas.web.mapper;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.web.dto.AuctionResponse;
import org.springframework.stereotype.Component;

@Component
public class AuctionResponseMapper {
    public AuctionResponse toResponse(Auction auction) {
        if (auction == null) {
            return null;
        }

        return new AuctionResponse(
                auction.getId(),
                auction.getProductId(),
                auction.getSellerId(),
                auction.getStartingPrice(),
                auction.getCurrentHighestBid(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auction.getWinnerId()
        );
    }
}