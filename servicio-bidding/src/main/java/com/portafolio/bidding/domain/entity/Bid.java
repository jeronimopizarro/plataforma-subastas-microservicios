package com.portafolio.bidding.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Bid {

    private final Long id;
    private final Long auctionId;
    private final Long bidderId;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    private Bid(Long id, Long auctionId, Long bidderId, BigDecimal amount, LocalDateTime timestamp) {
        this.id = id;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
        this.timestamp = timestamp;
        validate();
    }

    private void validate() {
        if (this.auctionId == null || this.bidderId == null) {
            throw new IllegalArgumentException("La puja debe estar asociada a una subasta y a un usuario.");
        }
        if (this.amount == null || this.amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto de la puja debe ser mayor a cero.");
        }
        if (this.timestamp == null) {
            throw new IllegalArgumentException("La puja debe tener una fecha y hora registrada.");
        }
    }

    public static Bid createNew(Long auctionId, Long bidderId, BigDecimal amount) {
        return new Bid(null, auctionId, bidderId, amount, LocalDateTime.now());
    }

    public static Bid restore(Long id, Long auctionId, Long bidderId, BigDecimal amount, LocalDateTime timestamp) {
        return new Bid(id, auctionId, bidderId, amount, timestamp);
    }

    public Long getId() { return id; }
    public Long getAuctionId() { return auctionId; }
    public Long getBidderId() { return bidderId; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}