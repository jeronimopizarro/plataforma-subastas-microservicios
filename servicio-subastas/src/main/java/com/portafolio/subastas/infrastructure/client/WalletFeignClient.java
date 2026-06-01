package com.portafolio.subastas.infrastructure.client;

import com.portafolio.subastas.infrastructure.client.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

// Le indicamos la URL exacta como hicimos en bidding
@FeignClient(name = "servicio-wallet", url = "http://servicio-wallet:8081", path = "/wallets")
public interface WalletFeignClient {

    // Cobrar definitivamente el dinero retenido del ganador
    @PostMapping("/{userId}/commit")
    void commitFunds(@PathVariable("userId") Long userId, @RequestBody TransactionRequest request);

    // Depositar la ganancia en la billetera del vendedor
    @PostMapping("/me/deposit")
    void depositFunds(@RequestHeader("X-User-Id") String userId, @RequestBody TransactionRequest request);
}