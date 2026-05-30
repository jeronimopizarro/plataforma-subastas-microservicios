package com.portafolio.subastas.domain.entity;

import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.exception.InvalidAuctionException;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Auction {

    private final Long id;
    private final Long productId;
    private final Long sellerId;
    private final BigDecimal startingPrice;
    private BigDecimal currentHighestBid;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private AuctionStatus status;
    private Long winnerId;

    private Auction(Long id, Long productId, Long sellerId, BigDecimal startingPrice,
                    BigDecimal currentHighestBid, LocalDateTime startTime, LocalDateTime endTime,
                    AuctionStatus status, Long winnerId) {
        this.id = id;
        this.productId = productId;
        this.sellerId = sellerId;
        this.startingPrice = startingPrice;
        this.currentHighestBid = currentHighestBid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status != null ? status : AuctionStatus.DRAFT;
        this.winnerId = winnerId;
        validate();
    }

    private void validate() {
        if (this.productId == null || this.sellerId == null) {
            throw new InvalidAuctionException("La subasta debe estar asociada a un producto y un vendedor.");
        }
        if (this.startingPrice == null || this.startingPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAuctionException("El precio inicial debe ser mayor a cero.");
        }
        if (this.startTime == null || this.endTime == null || this.endTime.isBefore(this.startTime)) {
            throw new InvalidAuctionException("Las fechas de la subasta son inválidas.");
        }
    }

    public static Auction createNew(Long productId, Long sellerId, BigDecimal startingPrice,
                                    LocalDateTime startTime, LocalDateTime endTime) {

        // Si la fecha de inicio es en el futuro, nace como SCHEDULED.
        // Si la fecha es ahora o en el pasado, nace directamente como ACTIVE.
        AuctionStatus initialStatus = startTime.isAfter(LocalDateTime.now())
                ? AuctionStatus.SCHEDULED
                : AuctionStatus.ACTIVE;

        return new Auction(null, productId, sellerId, startingPrice, startingPrice,
                startTime, endTime, initialStatus, null);
    }

    public static Auction restore(Long id, Long productId, Long sellerId, BigDecimal startingPrice,
                                  BigDecimal currentHighestBid, LocalDateTime startTime, LocalDateTime endTime,
                                  AuctionStatus status, Long winnerId) {
        return new Auction(id, productId, sellerId, startingPrice, currentHighestBid,
                startTime, endTime, status, winnerId);
    }

    public void start() {
        if (this.status != AuctionStatus.DRAFT && this.status != AuctionStatus.SCHEDULED) {
            throw new InvalidAuctionStateException("Solo las subastas en DRAFT o SCHEDULED pueden iniciarse.");
        }
        if (LocalDateTime.now().isAfter(this.endTime)) {
            throw new InvalidAuctionException("No se puede iniciar una subasta cuya fecha de fin ya pasó.");
        }
        this.status = AuctionStatus.ACTIVE;
    }

    public void updateBid(Long newWinnerId, BigDecimal newBidAmount) {
        if (this.status != AuctionStatus.ACTIVE) {
            throw new InvalidAuctionStateException("Solo se pueden registrar ofertas en subastas ACTIVAS.");
        }
        if (newBidAmount.compareTo(this.currentHighestBid) <= 0) {
            throw new InvalidAuctionException("El monto de la nueva oferta debe ser mayor a la oferta actual.");
        }
        this.currentHighestBid = newBidAmount;
        this.winnerId = newWinnerId;
    }

    public void finish(Long finalWinnerId, BigDecimal finalPrice) {
        if (this.status != AuctionStatus.ACTIVE) {
            throw new InvalidAuctionStateException("Solo las subastas ACTIVAS pueden finalizarse.");
        }

        this.status = AuctionStatus.FINISHED;
        this.winnerId = finalWinnerId;
        this.currentHighestBid = finalPrice;
    }

    public void cancel() {
        if (this.status == AuctionStatus.FINISHED) {
            throw new InvalidAuctionStateException("No se puede cancelar una subasta que ya finalizó.");
        }
        if (this.status == AuctionStatus.CANCELLED) {
            throw new InvalidAuctionStateException("La subasta ya se encuentra cancelada.");
        }

        this.status = AuctionStatus.CANCELLED;
    }
}
