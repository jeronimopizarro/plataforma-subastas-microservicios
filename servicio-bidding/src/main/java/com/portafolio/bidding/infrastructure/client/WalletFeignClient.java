package com.portafolio.bidding.infrastructure.client;

import com.portafolio.bidding.infrastructure.client.dto.TransactionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "servicio-wallet", url = "http://servicio-wallet:8081/wallets")
public interface WalletFeignClient {

    // Simula POST a http://localhost:8081/wallets/{userId}/hold
    @PostMapping("/{userId}/hold")
    void holdFunds(@PathVariable("userId") Long userId, @RequestBody TransactionRequest request);

    // Simula POST a http://localhost:8081/wallets/{userId}/release
    @PostMapping("/{userId}/release")
    void releaseFunds(@PathVariable("userId") Long userId, @RequestBody TransactionRequest request);

}