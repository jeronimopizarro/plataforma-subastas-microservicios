package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import com.portafolio.subastas.domain.exception.InvalidProductException;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateAuctionUseCase {

    private final AuctionRepository auctionRepository;
    private final ProductRepository productRepository;

    public CreateAuctionUseCase(AuctionRepository auctionRepository, ProductRepository productRepository) {
        this.auctionRepository = auctionRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Auction execute(CreateAuctionCommand command, String authUserId) {
        Product product = validateAndGetProduct(command.productId(), authUserId);

        validateNoActiveAuctionsForProduct(command.productId());

        Auction newAuction = Auction.createNew(
                command.productId(),
                product.getSellerId(),
                command.startingPrice(),
                command.startTime(),
                command.endTime()
        );

        return auctionRepository.save(newAuction);
    }

    private Product validateAndGetProduct(Long productId, String authUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.isActive()) {
            throw new InvalidProductException("El producto se encuentra inactivo.");
        }

        if (!product.getSellerId().equals(Long.parseLong(authUserId))) {
            throw new UnauthorizedAccessException("Acceso denegado: No eres el dueño de este producto.");
        }

        return product;
    }

    private void validateNoActiveAuctionsForProduct(Long productId) {
        if (auctionRepository.existsActiveAuctionForProduct(productId)) {
            throw new InvalidAuctionStateException("El producto ya se encuentra en una subasta DRAFT, SCHEDULED o ACTIVE.");
        }
    }

}