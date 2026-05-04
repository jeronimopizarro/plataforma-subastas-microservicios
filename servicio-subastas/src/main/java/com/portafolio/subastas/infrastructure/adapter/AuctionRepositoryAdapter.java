package com.portafolio.subastas.infrastructure.adapter;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.infrastructure.entity.AuctionEntity;
import com.portafolio.subastas.infrastructure.mapper.AuctionMapper;
import com.portafolio.subastas.infrastructure.repository.JpaAuctionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuctionRepositoryAdapter implements AuctionRepository {

    private final JpaAuctionRepository jpaRepository;
    private final AuctionMapper mapper;

    public AuctionRepositoryAdapter(JpaAuctionRepository jpaRepository, AuctionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Auction save(Auction auction) {
        AuctionEntity entity = mapper.toEntity(auction);
        AuctionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Auction> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}
