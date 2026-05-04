package com.portafolio.subastas.infrastructure.mapper;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.infrastructure.entity.AuctionEntity;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper {

    public AuctionEntity toEntity(Auction domain) {
        if (domain == null) {
            return null;
        }

        return AuctionEntity.builder()
                .id(domain.getId())
                .productId(domain.getProductId())
                .sellerId(domain.getSellerId())
                .startingPrice(domain.getStartingPrice())
                .currentHighestBid(domain.getCurrentHighestBid())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .status(domain.getStatus())
                .winnerId(domain.getWinnerId())
                .build();
    }

    public Auction toDomain(AuctionEntity entity) {
        if (entity == null) {
            return null;
        }

        return Auction.restore(
                entity.getId(),
                entity.getProductId(),
                entity.getSellerId(),
                entity.getStartingPrice(),
                entity.getCurrentHighestBid(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus(),
                entity.getWinnerId()
        );
    }
}
