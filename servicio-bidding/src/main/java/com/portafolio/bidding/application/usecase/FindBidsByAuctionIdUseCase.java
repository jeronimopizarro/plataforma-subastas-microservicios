package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.repository.BidRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindBidsByAuctionIdUseCase {

    private final BidRepository bidRepository;

    public FindBidsByAuctionIdUseCase(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public List<Bid> execute(Long auctionId) {
        return bidRepository.findByAuctionId(auctionId);
    }
}