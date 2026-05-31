package com.portafolio.bidding.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "servicio-usuarios", url = "http://servicio-usuarios:8083", path = "/users")
public interface UserFeignClient {

    @GetMapping("/{id}/email")
    Map<String, String> getUserEmail(@PathVariable("id") Long id);
}