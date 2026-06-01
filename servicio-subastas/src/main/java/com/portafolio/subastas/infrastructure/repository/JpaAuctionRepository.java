package com.portafolio.subastas.infrastructure.repository;

import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.infrastructure.entity.AuctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface JpaAuctionRepository extends JpaRepository<AuctionEntity, Long> {
    List<AuctionEntity> findByStatus(AuctionStatus status);

    List<AuctionEntity> findByStatusAndStartTimeLessThanEqual(AuctionStatus status,
                                                              LocalDateTime time);

    List<AuctionEntity> findByStatusAndEndTimeLessThanEqual(AuctionStatus status,
                                                            LocalDateTime time);

    boolean existsByProductIdAndStatusIn(Long productId, Collection<AuctionStatus> statuses);

    List<AuctionEntity> findByWinnerIdAndStatus(Long winnerId, AuctionStatus status);
}
