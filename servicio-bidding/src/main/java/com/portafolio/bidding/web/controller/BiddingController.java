package com.portafolio.bidding.web.controller;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.usecase.PlaceBidUseCase;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.web.dto.BidRequest;
import com.portafolio.bidding.web.dto.BidResponse;
import com.portafolio.bidding.web.mapper.BidResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bids")
public class BiddingController {

    private final PlaceBidUseCase placeBidUseCase;
    private final BidResponseMapper bidResponseMapper;

    public BiddingController(PlaceBidUseCase placeBidUseCase, BidResponseMapper bidResponseMapper) {
        this.placeBidUseCase = placeBidUseCase;
        this.bidResponseMapper = bidResponseMapper;
    }

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(
            @RequestBody BidRequest request,
            @RequestHeader("X-User-Id") String authUserId) {

        // Convertimos el ID seguro del Gateway a Long
        Long secureBidderId = Long.valueOf(authUserId);

        PlaceBidCommand command = new PlaceBidCommand(request.auctionId(), secureBidderId, request.amount());

        Bid savedBid = placeBidUseCase.execute(command, authUserId);
        BidResponse response = bidResponseMapper.toResponse(savedBid);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}