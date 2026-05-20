package com.portafolio.bidding.web.controller;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.usecase.PlaceBidUseCase;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.web.dto.BidRequest;
import com.portafolio.bidding.web.dto.BidResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bids")
public class BiddingController {

    private final PlaceBidUseCase placeBidUseCase;

    public BiddingController(PlaceBidUseCase placeBidUseCase) {
        this.placeBidUseCase = placeBidUseCase;
    }

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(@RequestBody BidRequest request) {
        PlaceBidCommand command = new PlaceBidCommand(request.auctionId(), request.bidderId(), request.amount());

        Bid savedBid = placeBidUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(createResponse(savedBid));
    }

    private static BidResponse createResponse(Bid savedBid) {
        return new BidResponse(
                savedBid.getId(),
                savedBid.getAuctionId(),
                savedBid.getBidderId(),
                savedBid.getAmount(),
                savedBid.getTimestamp()
        );
    }
}