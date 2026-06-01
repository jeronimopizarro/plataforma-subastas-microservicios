package com.portafolio.notificaciones.client;

import com.portafolio.notificaciones.client.dto.AuctionResponseDTO;
import com.portafolio.notificaciones.client.dto.ProductResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "servicio-subastas", url = "http://servicio-subastas:8080")
public interface AuctionClient {

    @GetMapping("/auctions/{id}")
    AuctionResponseDTO getAuctionById(@PathVariable("id") Long id);

    @GetMapping("/products/{id}")
    ProductResponseDTO getProductById(@PathVariable("id") Long id);
}