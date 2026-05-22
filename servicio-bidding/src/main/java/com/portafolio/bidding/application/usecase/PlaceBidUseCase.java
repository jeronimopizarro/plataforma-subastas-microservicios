package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.port.BidEventPublisher;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.exception.DomainException;
import com.portafolio.bidding.domain.exception.ErrorCode;
import com.portafolio.bidding.domain.repository.BidRepository;
import com.portafolio.bidding.infrastructure.client.AuctionFeignClient;
import com.portafolio.bidding.infrastructure.client.WalletFeignClient;
import com.portafolio.bidding.infrastructure.client.dto.AuctionResponse;
import com.portafolio.bidding.infrastructure.client.dto.TransactionRequest;
import com.portafolio.bidding.infrastructure.client.dto.UpdateBidRequest;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class PlaceBidUseCase {

    private final AuctionFeignClient auctionClient;
    private final WalletFeignClient walletClient;
    private final BidRepository bidRepository;
    private final BidEventPublisher bidEventPublisher;
    private static final Logger logger = LoggerFactory.getLogger(PlaceBidUseCase.class);

    public PlaceBidUseCase(AuctionFeignClient auctionClient,
                           WalletFeignClient walletClient,
                           BidRepository bidRepository, BidEventPublisher bidEventPublisher) {
        this.auctionClient = auctionClient;
        this.walletClient = walletClient;
        this.bidRepository = bidRepository;
        this.bidEventPublisher = bidEventPublisher;
    }

    @CircuitBreaker(name = "subastas", fallbackMethod = "fallbackPlaceBid")
    @Transactional
    public Bid execute(PlaceBidCommand command, String authUserId) {

        validateSameOwner(command, authUserId);

        AuctionResponse auction = fetchAndValidateAuction(command.auctionId(), command.amount());

        holdFundsForNewBidder(command.bidderId(), command.amount(), command.auctionId());

        try {
            updateAuctionCatalog(command.auctionId(), command.bidderId(), command.amount());

            releaseFundsForPreviousWinner(auction, command.auctionId());

            Bid savedBid = saveBidHistory(command.auctionId(), command.bidderId(), command.amount());

            bidEventPublisher.publishNewBid(savedBid.getAuctionId(), savedBid.getBidderId(), savedBid.getAmount());

            return savedBid;

        } catch (Exception e) {
            executeRollback(command.bidderId(), command.amount(), command.auctionId());
            throw new DomainException(
                    ErrorCode.INTERNAL_ERROR,
                    "Error al registrar la puja en el catálogo. Se devolvieron los fondos."
            );
        }
    }

    private void validateSameOwner(PlaceBidCommand command, String authUserId) {
        if (!command.bidderId().toString().equals(authUserId)) {
            throw new DomainException(ErrorCode.UNAUTHORIZED, "Error 403: No puedes realizar una puja a nombre de otro usuario.");
        }
    }


    private AuctionResponse fetchAndValidateAuction(Long auctionId, BigDecimal amount) {
        AuctionResponse auction = auctionClient.getAuctionById(auctionId);

        if (!"ACTIVE".equals(auction.status())) {
            throw new DomainException(ErrorCode.INVALID_ARGUMENT, "La subasta no está activa.");
        }
        if (amount.compareTo(auction.currentHighestBid()) <= 0) {
            throw new DomainException(ErrorCode.INVALID_ARGUMENT, "El monto debe ser mayor a la oferta actual de $" + auction.currentHighestBid());
        }
        return auction;
    }

    private void holdFundsForNewBidder(Long bidderId, BigDecimal amount, Long auctionId) {
        walletClient.holdFunds(bidderId, new TransactionRequest(amount, "Puja en subasta: " + auctionId));
    }

    private void updateAuctionCatalog(Long auctionId, Long bidderId, BigDecimal amount) {
        auctionClient.updateCurrentBid(auctionId, new UpdateBidRequest(bidderId, amount));
    }

    private void releaseFundsForPreviousWinner(AuctionResponse auction, Long auctionId) {
        if (auction.winnerId() != null) {
            walletClient.releaseFunds(
                    auction.winnerId(),
                    new TransactionRequest(auction.currentHighestBid(), "Puja superada en subasta: " + auctionId)
            );
        }
    }

    private Bid saveBidHistory(Long auctionId, Long bidderId, BigDecimal amount) {
        Bid newBid = Bid.createNew(auctionId, bidderId, amount);
        return bidRepository.save(newBid);
    }

    private void executeRollback(Long bidderId, BigDecimal amount, Long auctionId) {
        walletClient.releaseFunds(bidderId, new TransactionRequest(amount, "Devolución por error en puja: " + auctionId));
    }

    public Bid fallbackPlaceBid(PlaceBidCommand command, String authUserId, Throwable t) {

        // Si el error es una regla de negocio nuestra
        // simplemente lo volvemos a lanzar tal cual para que el Handler lo convierta en 400.
        if (t instanceof DomainException domainException) {
            throw domainException;
        }

        // 1. Verificamos si el error viene de Feign (Errores HTTP de otros servicios)
        if (t instanceof feign.FeignException feignException) {
            int status = feignException.status();

            if (status >= 400 && status < 500) {
                logger.warn("El servicio externo rechazó la operación (Status {}). Motivo: {}", status, feignException.getMessage());
                throw new DomainException(
                        ErrorCode.INVALID_ARGUMENT,
                        "No se pudo procesar la operación. Verifique los datos enviados."
                );
            }
        }

        // 2. Fallo real de infraestructura (500 o Connection Refused)
        logger.error("¡Circuit Breaker activado! La red o el servidor fallaron. Motivo: {}", t.getMessage());

        throw new DomainException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "El servicio se encuentra saturado o fuera de línea. Por favor, intente en unos segundos."
        );
    }
}