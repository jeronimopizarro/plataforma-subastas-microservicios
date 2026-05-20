package com.portafolio.bidding.infrastructure.mapper;

import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.infrastructure.entity.BidEntity;

public class BidMapper {

    public static BidEntity toEntity(Bid domain) {
        return new BidEntity(
                domain.getId(),
                domain.getAuctionId(),
                domain.getBidderId(),
                domain.getAmount(),
                domain.getTimestamp()
        );
    }

    public static Bid toDomain(BidEntity entity) {
        return Bid.restore(
                entity.getId(),
                entity.getAuctionId(),
                entity.getBidderId(),
                entity.getAmount(),
                entity.getTimestamp()
        );
    }
}