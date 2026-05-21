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
        // 1. Obtener y validar el producto (existencia, estado activo y propiedad)
        Product product = validateAndGetProduct(command.productId(), authUserId);

        // 2. Validar que no haya subastas activas
        validateNoActiveAuctionsForProduct(command.productId());

        // 3. Crear subasta garantizando que el sellerId es el dueño real del producto
        Auction newAuction = buildAuctionFrom(command, product.getSellerId());
        return auctionRepository.save(newAuction);
    }

    private Product validateAndGetProduct(Long productId, String authUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.isActive()) {
            throw new InvalidProductException("El producto se encuentra inactivo y no puede ser subastado.");
        }

        if (!product.getSellerId().toString().equals(authUserId)) {
            throw new UnauthorizedAccessException("Error 403: No puedes crear una subasta para un producto que no te pertenece.");
        }

        return product;
    }

    private void validateNoActiveAuctionsForProduct(Long productId) {
        if (auctionRepository.existsActiveAuctionForProduct(productId)) {
            throw new InvalidAuctionStateException("El producto ya se encuentra en una subasta DRAFT o ACTIVE.");
        }
    }

    private Auction buildAuctionFrom(CreateAuctionCommand command, Long safeSellerId) {
        return Auction.createNew(
                command.productId(),
                safeSellerId, // Inyectamos el ID validado directamente
                command.startingPrice(),
                command.startTime(),
                command.endTime()
        );
    }
}