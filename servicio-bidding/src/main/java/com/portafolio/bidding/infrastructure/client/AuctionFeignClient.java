package com.portafolio.bidding.infrastructure.client;

import com.portafolio.bidding.infrastructure.client.dto.AuctionResponse;
import com.portafolio.bidding.infrastructure.client.dto.UpdateBidRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

// Le indicamos el nombre del servicio y la URL base donde vive el Catálogo
@FeignClient(name = "servicio-subastas", url = "http://servicio-subastas:8080/auctions")
public interface AuctionFeignClient {

    // Simula una petición GET a http://localhost:8080/auctions/{id}
    @GetMapping("/{id}")
    AuctionResponse getAuctionById(@PathVariable("id") Long id);

    // Simula una petición PATCH a http://localhost:8080/auctions/{id}/current-bid
    @PatchMapping("/{id}/current-bid")
    void updateCurrentBid(@PathVariable("id") Long id, @RequestBody UpdateBidRequest request);
}