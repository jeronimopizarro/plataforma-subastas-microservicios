package com.portafolio.bidding.web.controller;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.usecase.FindBidsByAuctionIdUseCase;
import com.portafolio.bidding.application.usecase.FindBidsByUserIdUseCase;
import com.portafolio.bidding.application.usecase.PlaceBidUseCase;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.infrastructure.client.UserFeignClient;
import com.portafolio.bidding.web.dto.BidRequest;
import com.portafolio.bidding.web.dto.BidResponse;
import com.portafolio.bidding.web.mapper.BidResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bids")
public class BiddingController {

    private final PlaceBidUseCase placeBidUseCase;
    private final FindBidsByUserIdUseCase findBidsByUserIdUseCase;
    private final FindBidsByAuctionIdUseCase findBidsByAuctionIdUseCase;
    private final BidResponseMapper bidResponseMapper;
    private final UserFeignClient userFeignClient;

    public BiddingController(PlaceBidUseCase placeBidUseCase, FindBidsByUserIdUseCase findBidsByUserIdUseCase, FindBidsByAuctionIdUseCase findBidsByAuctionIdUseCase, BidResponseMapper bidResponseMapper, UserFeignClient userFeignClient) {
        this.placeBidUseCase = placeBidUseCase;
        this.findBidsByUserIdUseCase = findBidsByUserIdUseCase;
        this.findBidsByAuctionIdUseCase = findBidsByAuctionIdUseCase;
        this.bidResponseMapper = bidResponseMapper;
        this.userFeignClient = userFeignClient;
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

    @GetMapping("/mine")
    public ResponseEntity<List<BidResponse>> getMyBids(@RequestHeader("X-User-Id") String userId) {
        List<Bid> bids = findBidsByUserIdUseCase.execute(Long.valueOf(userId));

        List<BidResponse> response = bids.stream()
                .map(bidResponseMapper::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auction/{auctionId}")
    @Operation(summary = "Obtener el historial de pujas de una subasta específica")
    public ResponseEntity<List<BidResponse>> getBidsByAuctionId(@PathVariable Long auctionId) {
        List<Bid> bids = findBidsByAuctionIdUseCase.execute(auctionId);

        // Caché temporal para no repetir llamadas HTTP por el mismo usuario
        Map<Long, String> emailCache = new HashMap<>();

        List<BidResponse> responses = bids.stream().map(bid -> {
            String email = emailCache.computeIfAbsent(bid.getBidderId(), id -> {
                try {
                    return userFeignClient.getUserEmail(id).get("email");
                } catch (Exception e) {
                    return "usuario@desconocido.com";
                }
            });

            return new BidResponse(
                    bid.getId(),
                    bid.getAuctionId(),
                    bid.getBidderId(),
                    maskEmail(email), // <-- Aplicamos la máscara
                    bid.getAmount(),
                    bid.getTimestamp()
            );
        }).toList();

        return ResponseEntity.ok(responses);
    }

    // Método privado para ocultar parte del correo
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 3) return name + "***@" + domain;
        return name.substring(0, 3) + "***@" + domain;
    }
}