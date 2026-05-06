package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StartAuctionsUseCase {

    private final AuctionRepository auctionRepository;

    public StartAuctionsUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public void execute() {
        LocalDateTime now = LocalDateTime.now();

        List<Auction> pendingAuctions = auctionRepository.findAuctionsToStart(now);

        for (Auction auction : pendingAuctions) {
            try {
                auction.start();

                auctionRepository.save(auction);

                System.out.println("Subasta " + auction.getId() + " iniciada automáticamente.");
            } catch (Exception e) {
                System.err.println("Error al iniciar subasta " + auction.getId() + ": " + e.getMessage());
            }
        }
    }
}
