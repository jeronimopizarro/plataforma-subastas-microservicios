package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.repository.BidRepository;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FindBidsByUserIdUseCase {
    private final BidRepository bidRepository;

    public FindBidsByUserIdUseCase(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
    }

    public List<Bid> execute(Long userId) {
        return bidRepository.findByBidderId(userId);
    }
}