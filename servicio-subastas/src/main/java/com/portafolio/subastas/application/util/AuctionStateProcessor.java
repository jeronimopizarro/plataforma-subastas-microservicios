package com.portafolio.subastas.application.util;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

@Component
public class AuctionStateProcessor {

    private final AuctionRepository auctionRepository;

    public AuctionStateProcessor(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public void processBatch(List<Auction> auctions, Consumer<Auction> action, String actionName) {
        for (Auction auction : auctions) {
            try {
                // Ejecutamos la acción (start o finish)
                action.accept(auction);

                auctionRepository.save(auction);

                System.out.println("Subasta " + auction.getId() + " " + actionName + " exitosamente.");
            } catch (Exception e) {
                System.err.println("Error al " + actionName + " la subasta " + auction.getId() + ": " + e.getMessage());
            }
        }
    }
}
