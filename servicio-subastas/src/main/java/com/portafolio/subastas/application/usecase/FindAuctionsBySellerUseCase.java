package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindAuctionsBySellerUseCase {

    private final AuctionRepository auctionRepository;

    public FindAuctionsBySellerUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public List<Auction> execute(Long sellerId) {
        return auctionRepository.findAuctionsBySellerId(sellerId);
    }
}