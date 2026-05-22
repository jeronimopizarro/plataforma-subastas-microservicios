package com.portafolio.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TrackingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        // 1. Verificamos si la petición ya trae un Correlation ID (ej. de otro servicio interno)
        String correlationId;
        if (headers.containsKey(CORRELATION_ID_HEADER)) {
            correlationId = headers.getFirst(CORRELATION_ID_HEADER);
            logger.info("Petición recibida con Correlation ID existente: {}", correlationId);
        } else {
            // 2. Si no lo trae (es una petición nueva del frontend), generamos uno
            correlationId = UUID.randomUUID().toString();
            logger.info("Generando nuevo Correlation ID: {}", correlationId);

            // 3. Mutamos la petición para inyectarle la cabecera antes de enviarla a los microservicios
            exchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header(CORRELATION_ID_HEADER, correlationId)
                            .build())
                    .build();
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        // Queremos que este filtro se ejecute MUY temprano, antes que la autenticación
        return -1;
    }
}