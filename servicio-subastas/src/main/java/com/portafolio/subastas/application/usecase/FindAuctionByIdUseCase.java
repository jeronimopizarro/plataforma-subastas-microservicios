package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.exception.AuctionNotFoundException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;

@Service
public class FindAuctionByIdUseCase {
    private final AuctionRepository auctionRepository;

    public FindAuctionByIdUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction execute(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));
    }
}
