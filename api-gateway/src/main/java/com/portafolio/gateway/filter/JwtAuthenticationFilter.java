package com.portafolio.gateway.filter;

import com.portafolio.gateway.security.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        super(Config.class);
        this.jwtUtils = jwtUtils;
    }

    public static class Config {
        // Clase de configuración obligatoria para los filtros de Spring Cloud Gateway
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            // 1. Verificamos si la petición tiene la cabecera "Authorization"
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete(); // Lo rebotamos con un 401
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            // 2. Verificamos que el token tenga el formato correcto ("Bearer eyJ...")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 3. Extraemos el token puro (quitando la palabra "Bearer ")
            String token = authHeader.substring(7);

            // 4. Validamos que el token sea auténtico y no haya expirado
            if (!jwtUtils.validateJwtToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // 5. [Nivel Arquitectura Pro]: Extraemos el ID del usuario del token
            // y lo inyectamos en una nueva cabecera para que el microservicio de destino sepa quién es.
            String userId = jwtUtils.getUserIdFromJwtToken(token);
            exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();

            // 6. ¡Todo en orden! Le decimos a la cadena que deje pasar la petición
            return chain.filter(exchange);
        };
    }
}