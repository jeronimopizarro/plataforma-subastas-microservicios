package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.domain.repository.ProductRepository;

public class CreateAuctionUseCase {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    public CreateAuctionUseCase(AuctionRepository auctionRepository, ProductRepository productRepository) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
    }

    public Auction execute(CreateAuctionCommand command) {
        ensureProductIsAvailable(command.productId());

        Auction newAuction = buildAuctionFrom(command);

        return auctionRepository.save(newAuction);
    }

    private void ensureProductIsAvailable(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("El producto con ID " + productId + " no existe."));
    }

    private Auction buildAuctionFrom(CreateAuctionCommand command) {
        return Auction.createNew(
                command.productId(),
                command.sellerId(),
                command.startingPrice(),
                command.startTime(),
                command.endTime()
        );
    }
}
