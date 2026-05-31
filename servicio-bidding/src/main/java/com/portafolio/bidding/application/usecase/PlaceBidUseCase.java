package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.port.BidEventPublisher;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.exception.DomainException;
import com.portafolio.bidding.domain.exception.ErrorCode;
import com.portafolio.bidding.domain.repository.BidRepository;
import com.portafolio.bidding.infrastructure.client.AuctionFeignClient;
import com.portafolio.bidding.infrastructure.client.UserFeignClient;
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
    private final UserFeignClient userFeignClient;
    private static final Logger logger = LoggerFactory.getLogger(PlaceBidUseCase.class);

    public PlaceBidUseCase(AuctionFeignClient auctionClient,
                           WalletFeignClient walletClient,
                           BidRepository bidRepository,
                           BidEventPublisher bidEventPublisher,
                           UserFeignClient userFeignClient) {
        this.auctionClient = auctionClient;
        this.walletClient = walletClient;
        this.bidRepository = bidRepository;
        this.bidEventPublisher = bidEventPublisher;
        this.userFeignClient = userFeignClient;
    }

    @CircuitBreaker(name = "subastas", fallbackMethod = "fallbackPlaceBid")
    @Transactional
    public Bid execute(PlaceBidCommand command, String authUserId) {

        validateSameOwner(command, authUserId);

        AuctionResponse auction = fetchAndValidateAuction(command.auctionId(), command.amount(), command.bidderId());

        holdFundsForNewBidder(command.bidderId(), command.amount(), command.auctionId());

        try {
            updateAuctionCatalog(command.auctionId(), command.bidderId(), command.amount());

            releaseFundsForPreviousWinner(auction, command.auctionId());

            Bid savedBid = saveBidHistory(command.auctionId(), command.bidderId(), command.amount());

            String rawEmail;
            try {
                rawEmail = userFeignClient.getUserEmail(savedBid.getBidderId()).get("email");
            } catch (Exception e) {
                // CAMBIO TEMPORAL PARA DEBUGEAR
                logger.error("¡ERROR EN FEIGN! El servicio de usuarios falló al consultar el ID {}. Error: {}",
                        savedBid.getBidderId(), e.getMessage());
                rawEmail = "error-de-comunicacion@sistema.com";
            }

            String maskedEmail = maskEmail(rawEmail);

            // Actualizamos la llamada al publicador agregando el maskedEmail
            bidEventPublisher.publishNewBid(savedBid.getAuctionId(), savedBid.getBidderId(), maskedEmail, savedBid.getAmount());
            // --- FIN NUEVA LÓGICA --
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

    private AuctionResponse fetchAndValidateAuction(Long auctionId, BigDecimal amount, Long bidderId) {
        AuctionResponse auction = auctionClient.getAuctionById(auctionId);

        if (auction.sellerId() != null && auction.sellerId().equals(bidderId)) {
            throw new DomainException(ErrorCode.INVALID_ARGUMENT, "Operación denegada: No puedes pujar en tu propia subasta.");
        }

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

    // --- NUEVO MÉTODO PRIVADO PARA ENMASCARAR ---
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 3) return name + "***@" + domain;
        return name.substring(0, 3) + "***@" + domain;
    }

    public Bid fallbackPlaceBid(PlaceBidCommand command, String authUserId, Throwable t) {
        if (t instanceof DomainException domainException) {
            throw domainException;
        }

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

        logger.error("¡Circuit Breaker activado! La red o el servidor fallaron. Motivo: {}", t.getMessage());

        throw new DomainException(
                ErrorCode.SERVICE_UNAVAILABLE,
                "El servicio se encuentra saturado o fuera de línea. Por favor, intente en unos segundos."
        );
    }
}