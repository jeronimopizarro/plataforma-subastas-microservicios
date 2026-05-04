package com.portafolio.subastas.web.controller;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.application.usecase.CreateAuctionUseCase;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.web.dto.AuctionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final CreateAuctionUseCase createAuctionUseCase;

    public AuctionController(CreateAuctionUseCase createAuctionUseCase) {
        this.createAuctionUseCase = createAuctionUseCase;
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@RequestBody CreateAuctionCommand command) {
        Auction savedAuction = createAuctionUseCase.execute(command);
        AuctionResponse response = mapToResponse(savedAuction);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private AuctionResponse mapToResponse(Auction auction) {
        return new AuctionResponse(
                auction.getId(),
                auction.getProductId(),
                auction.getSellerId(),
                auction.getStartingPrice(),
                auction.getCurrentHighestBid(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auction.getWinnerId()
        );
    }
}
