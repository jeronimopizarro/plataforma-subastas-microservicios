package com.portafolio.subastas.infrastructure.adapter;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.infrastructure.entity.AuctionEntity;
import com.portafolio.subastas.infrastructure.mapper.AuctionMapper;
import com.portafolio.subastas.infrastructure.repository.JpaAuctionRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Override
    public List<Auction> findByStatus(AuctionStatus status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Auction> findAuctionsToStart(LocalDateTime currentTime) {
        // CAMBIO: Ahora el Scheduler busca las SCHEDULED para iniciarlas
        return jpaRepository.findByStatusAndStartTimeLessThanEqual(AuctionStatus.SCHEDULED, currentTime)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Auction> findAuctionsToFinish(LocalDateTime currentTime) {
        return jpaRepository.findByStatusAndEndTimeLessThanEqual(AuctionStatus.ACTIVE, currentTime)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsActiveAuctionForProduct(Long productId) {
        return jpaRepository.existsByProductIdAndStatusIn(
                productId,
                List.of(AuctionStatus.DRAFT, AuctionStatus.SCHEDULED, AuctionStatus.ACTIVE)
        );
    }

    @Override
    public List<Auction> findByWinnerId(Long winnerId) {
        return jpaRepository.findByWinnerId(winnerId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
