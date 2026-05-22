package com.portafolio.bidding.infrastructure.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    private static final String CORRELATION_ID_LOG_VAR = "correlationId";

    @Bean
    public RequestInterceptor correlationIdInterceptor() {
        return template -> {
            // 1. Buscamos el ID en la mochila del hilo actual
            String correlationId = MDC.get(CORRELATION_ID_LOG_VAR);

            // 2. Si existe, se lo pegamos a la cabecera de la petición saliente de Feign
            if (correlationId != null) {
                template.header(CORRELATION_ID_HEADER, correlationId);
            }

        };
    }
}