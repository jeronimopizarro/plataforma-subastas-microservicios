package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FindWonAuctionsUseCase {
    private final AuctionRepository auctionRepository;

    public FindWonAuctionsUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public List<Auction> execute(Long winnerId) {
        return auctionRepository.findByWinnerIdAndStatus(winnerId, AuctionStatus.FINISHED);
    }
}