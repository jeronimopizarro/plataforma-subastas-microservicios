package com.portafolio.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TrackingFilterTest {

    private TrackingFilter trackingFilter;
    private GatewayFilterChain filterChain;

    @BeforeEach
    void setUp() {
        trackingFilter = new TrackingFilter();

        filterChain = mock(GatewayFilterChain.class);

        when(filterChain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("Debe inyectar un nuevo X-Correlation-Id si la petición no lo tiene")
    void inyectarNuevoCorrelationId() {
        // Creamos una petición HTTP simulada SIN cabeceras
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        // Hacemos pasar la petición por nuestro filtro
        trackingFilter.filter(exchange, filterChain).block(); // block() es necesario porque es código reactivo

        // Capturamos la petición mutada que el filtro le pasó al siguiente eslabón
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(captor.capture());
        ServerWebExchange mutatedExchange = captor.getValue();

        // Comprobamos que el filtro le haya inyectado un Correlation ID
        String correlationId = mutatedExchange.getRequest().getHeaders().getFirst("X-Correlation-Id");

        assertNotNull(correlationId, "El Correlation ID no debe ser nulo");
        assertFalse(correlationId.isEmpty(), "El Correlation ID no debe estar vacío");
        System.out.println("Test 1 - ID Generado: " + correlationId);
    }

    @Test
    @DisplayName("Debe mantener el X-Correlation-Id existente si el cliente ya lo envió")
    void mantenerCorrelationIdExistente() {
        //  Creamos una petición HTTP que YA TRAE un ID desde el cliente
        String idOriginal = "mi-id-personalizado-999";
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test")
                .header("X-Correlation-Id", idOriginal)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        //  Pasamos la petición por el filtro
        trackingFilter.filter(exchange, filterChain).block();

        //  Capturamos la petición mutada
        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        verify(filterChain).filter(captor.capture());
        ServerWebExchange mutatedExchange = captor.getValue();

        // Comprobamos que el ID siga siendo el original y no haya sido pisado
        String correlationIdFinal = mutatedExchange.getRequest().getHeaders().getFirst("X-Correlation-Id");

        assertEquals(idOriginal, correlationIdFinal, "El filtro no debe sobrescribir un ID existente");
        System.out.println("Test 2 - ID Mantenido: " + correlationIdFinal);
    }
}