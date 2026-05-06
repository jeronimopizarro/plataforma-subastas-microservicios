package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import com.portafolio.subastas.domain.exception.InvalidProductException;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateAuctionUseCase {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    public CreateAuctionUseCase(AuctionRepository auctionRepository, ProductRepository productRepository) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
    }

    public Auction execute(CreateAuctionCommand command) {
        ensureProductIsActive(command.productId());

        validateNoActiveAuctionsForProduct(command.productId());

        Auction newAuction = buildAuctionFrom(command);
        return auctionRepository.save(newAuction);
    }

    private void ensureProductIsActive(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.isActive()) {
            throw new InvalidProductException("El producto se encuentra inactivo y no puede ser subastado.");
        }
    }

    private void validateNoActiveAuctionsForProduct(Long productId) {
        if (auctionRepository.existsActiveAuctionForProduct(productId)) {
            throw new InvalidAuctionStateException("El producto ya se encuentra en una subasta DRAFT o ACTIVE.");
        }
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
