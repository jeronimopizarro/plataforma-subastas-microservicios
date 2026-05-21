package com.portafolio.bidding.web.mapper;

import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.web.dto.BidResponse;
import org.springframework.stereotype.Component;

@Component
public class BidResponseMapper {

    public BidResponse toResponse(Bid bid) {
        if (bid == null) {
            return null;
        }

        return new BidResponse(
                bid.getId(),
                bid.getAuctionId(),
                bid.getBidderId(),
                bid.getAmount(),
                bid.getTimestamp()
        );
    }
}