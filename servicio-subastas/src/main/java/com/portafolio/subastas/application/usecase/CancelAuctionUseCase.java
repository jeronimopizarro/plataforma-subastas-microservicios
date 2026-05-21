package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.exception.AuctionNotFoundException;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelAuctionUseCase {

    private final AuctionRepository auctionRepository;

    public CancelAuctionUseCase(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    @Transactional
    public void execute(Long id, String authUserId) {
        Auction auction = findAuctionOrThrow(id);

        validateSameOwner(authUserId, auction);

        auction.cancel();

        auctionRepository.save(auction);
    }

    private static void validateSameOwner(String authUserId, Auction auction) {
        if (!auction.getSellerId().toString().equals(authUserId)) {
            throw new UnauthorizedAccessException("Error 403: No tienes permisos para cancelar esta subasta.");
        }
    }

    private Auction findAuctionOrThrow(Long id) {
        return auctionRepository.findById(id)
                .orElseThrow(() -> new AuctionNotFoundException(id));
    }
}