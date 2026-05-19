package com.portafolio.subastas.web.controller;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.application.usecase.*;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.web.dto.AuctionResponse;
import com.portafolio.subastas.web.dto.UpdateBidRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final CreateAuctionUseCase createAuctionUseCase;
    private final FindAuctionByIdUseCase findAuctionByIdUseCase;
    private final ListAuctionsByStatusUseCase listAuctionsByStatusUseCase;
    private final CancelAuctionUseCase cancelAuctionUseCase;
    private final UpdateAuctionBidUseCase updateAuctionBidUseCase;

    public AuctionController(CreateAuctionUseCase createAuctionUseCase,
                             FindAuctionByIdUseCase findAuctionByIdUseCase,
                             ListAuctionsByStatusUseCase listAuctionsByStatusUseCase,
                             CancelAuctionUseCase cancelAuctionUseCase, UpdateAuctionBidUseCase updateAuctionBidUseCase) {
        this.createAuctionUseCase = createAuctionUseCase;
        this.findAuctionByIdUseCase = findAuctionByIdUseCase;
        this.listAuctionsByStatusUseCase = listAuctionsByStatusUseCase;
        this.cancelAuctionUseCase = cancelAuctionUseCase;
        this.updateAuctionBidUseCase = updateAuctionBidUseCase;
    }

    @PostMapping
    public ResponseEntity<AuctionResponse> createAuction(@RequestBody CreateAuctionCommand command) {
        Auction savedAuction = createAuctionUseCase.execute(command);
        AuctionResponse response = mapToResponse(savedAuction);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponse> getAuctionById(@PathVariable Long id) {
        Auction auction = findAuctionByIdUseCase.execute(id);
        return ResponseEntity.ok(mapToResponse(auction));
    }

    // Si el usuario no manda el parámetro, por defecto buscamos las ACTIVE.
    @GetMapping
    public ResponseEntity<List<AuctionResponse>> getAuctionsByStatus(
            @RequestParam(defaultValue = "ACTIVE") AuctionStatus status) {

        List<AuctionResponse> auctions = listAuctionsByStatusUseCase.execute(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(auctions);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelAuction(@PathVariable Long id) {
        cancelAuctionUseCase.execute(id);
        return ResponseEntity.noContent().build();
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

    @PatchMapping("/{id}/current-bid")
    public ResponseEntity<Void> updateCurrentBid(
            @PathVariable Long id,
            @RequestBody UpdateBidRequest request) {
        updateAuctionBidUseCase.execute(id, request.winnerId(), request.amount());
        return ResponseEntity.noContent().build();
    }
}
