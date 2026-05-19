package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.exception.AuctionNotFoundException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UpdateAuctionBidUseCase {

    private final AuctionRepository auctionRepository;

    public UpdateAuctionBidUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public void execute(Long auctionId, Long winnerId, BigDecimal amount) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        auction.updateBid(winnerId, amount);

        auctionRepository.save(auction);
    }
}