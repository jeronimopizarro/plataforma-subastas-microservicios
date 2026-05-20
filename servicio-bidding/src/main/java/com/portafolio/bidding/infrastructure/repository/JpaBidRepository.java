package com.portafolio.bidding.infrastructure.repository;

import com.portafolio.bidding.infrastructure.entity.BidEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaBidRepository extends JpaRepository<BidEntity, Long> {
    List<BidEntity> findByAuctionIdOrderByAmountDesc(Long auctionId);
}