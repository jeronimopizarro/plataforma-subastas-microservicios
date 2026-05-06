package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.util.AuctionStateProcessor;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FinishAuctionsUseCase {

    private final AuctionRepository auctionRepository;
    private final AuctionStateProcessor stateProcessor;

    public FinishAuctionsUseCase(AuctionRepository auctionRepository,
                                 AuctionStateProcessor stateProcessor) {
        this.auctionRepository = auctionRepository;
        this.stateProcessor = stateProcessor;
    }

    public void execute() {
        List<Auction> pendingToFinish = auctionRepository.findAuctionsToFinish(LocalDateTime.now());

        // Usamos una lambda para pasar los parámetros requeridos por tu método finish
        stateProcessor.processBatch(
                pendingToFinish,
                auction -> auction.finish(auction.getWinnerId(), auction.getCurrentHighestBid()),
                "finalizada"
        );
    }
}
