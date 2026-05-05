package com.portafolio.subastas.infrastructure.repository;

import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.infrastructure.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaAuctionRepository extends JpaRepository<AuctionEntity, Long> {
    List<AuctionEntity> findByStatus(AuctionStatus status);
}
