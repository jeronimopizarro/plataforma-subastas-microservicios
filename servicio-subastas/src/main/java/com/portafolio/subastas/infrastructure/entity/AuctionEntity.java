package com.portafolio.subastas.infrastructure.entity;

import com.portafolio.subastas.domain.enums.AuctionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "starting_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal startingPrice;

    @Column(name = "current_highest_bid", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentHighestBid;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // Enum como String (DRAFT, ACTIVE, etc.)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuctionStatus status;

    @Column(name = "winner_id")
    private Long winnerId; // Es nulo hasta que la subasta finaliza
}
