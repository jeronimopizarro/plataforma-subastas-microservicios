package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAuctionsByStatusUseCase {
    private final AuctionRepository auctionRepository;

    public ListAuctionsByStatusUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public List<Auction> execute(AuctionStatus status) {
        return auctionRepository.findByStatus(status);
    }
}
