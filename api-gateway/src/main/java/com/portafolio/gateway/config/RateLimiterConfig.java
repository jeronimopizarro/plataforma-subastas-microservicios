package com.portafolio.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // Buscamos si el filtro JWT ya puso el ID del usuario en la cabecera
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");

            // Si tiene ID, lo limitamos por usuario.
            // Si no tiene (ej. rutas públicas como login), usamos su IP como respaldo.
            return Mono.just(userId != null ? userId :
                    exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }
}