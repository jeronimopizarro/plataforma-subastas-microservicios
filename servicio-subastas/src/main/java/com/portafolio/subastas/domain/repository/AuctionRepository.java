package com.portafolio.subastas.domain.repository;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuctionRepository {
    Auction save(Auction auction);
    Optional<Auction> findById(Long id);
    List<Auction> findByStatus(AuctionStatus status);
    List<Auction> findAuctionsToStart(LocalDateTime currentTime);
}
