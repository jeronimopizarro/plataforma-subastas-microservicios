package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.exception.AuctionNotFoundException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;

@Service
public class CancelAuctionUseCase {

    private final AuctionRepository auctionRepository;

    public CancelAuctionUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public void execute(Long id) {
        Auction auction = findAuctionOrThrow(id);

        auction.cancel();

        auctionRepository.save(auction);
    }

    private Auction findAuctionOrThrow(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));
    }
}
