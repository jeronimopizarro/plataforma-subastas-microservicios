package com.portafolio.subastas.domain.repository;

import com.portafolio.subastas.domain.entity.Auction;

import java.util.Optional;

public interface AuctionRepository {
    Auction save(Auction auction);
    Optional<Auction> findById(Long id);
}
