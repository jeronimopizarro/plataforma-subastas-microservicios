package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.port.BidEventPublisher;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.repository.BidRepository;
import com.portafolio.bidding.infrastructure.client.AuctionFeignClient;
import com.portafolio.bidding.infrastructure.client.WalletFeignClient;
import com.portafolio.bidding.infrastructure.client.dto.AuctionResponse;
import com.portafolio.bidding.infrastructure.client.dto.TransactionRequest;
import com.portafolio.bidding.infrastructure.client.dto.UpdateBidRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PlaceBidUseCase {

    private final AuctionFeignClient auctionClient;
    private final WalletFeignClient walletClient;
    private final BidRepository bidRepository;
    private final BidEventPublisher bidEventPublisher;

    public PlaceBidUseCase(AuctionFeignClient auctionClient,
                           WalletFeignClient walletClient,
                           BidRepository bidRepository, BidEventPublisher bidEventPublisher) {
        this.auctionClient = auctionClient;
        this.walletClient = walletClient;
        this.bidRepository = bidRepository;
        this.bidEventPublisher = bidEventPublisher;
    }

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
            throw new RuntimeException("Error al registrar la puja en el catálogo. Se devolvieron los fondos.", e);
        }
    }

    private void validateSameOwner(PlaceBidCommand command, String authUserId) {
        if (!command.bidderId().toString().equals(authUserId)) {
            throw new RuntimeException("Error 403: No puedes realizar una puja a nombre de otro usuario.");
        }
    }


    private AuctionResponse fetchAndValidateAuction(Long auctionId, BigDecimal amount) {
        AuctionResponse auction = auctionClient.getAuctionById(auctionId);

        if (!"ACTIVE".equals(auction.status())) {
            throw new IllegalStateException("La subasta no está activa.");
        }
        if (amount.compareTo(auction.currentHighestBid()) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a la oferta actual de $" + auction.currentHighestBid());
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
}