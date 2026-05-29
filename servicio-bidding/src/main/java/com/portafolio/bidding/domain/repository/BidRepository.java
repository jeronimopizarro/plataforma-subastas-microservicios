package com.portafolio.bidding.domain.repository;

import com.portafolio.bidding.domain.entity.Bid;

import java.util.List;

public interface BidRepository {

    Bid save(Bid bid);

    List<Bid> findByAuctionId(Long auctionId);

    List<Bid> findByBidderId(Long bidderId);
}