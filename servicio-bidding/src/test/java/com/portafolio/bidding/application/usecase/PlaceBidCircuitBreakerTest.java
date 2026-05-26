package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.domain.exception.DomainException;
import com.portafolio.bidding.domain.exception.ErrorCode;
import com.portafolio.bidding.infrastructure.client.AuctionFeignClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class PlaceBidCircuitBreakerTest {

    @Autowired
    private PlaceBidUseCase placeBidUseCase;

    // Reemplazamos el cliente real por uno falso para no salir a la red
    @MockitoBean
    private AuctionFeignClient auctionClient;

    @Test
    @DisplayName("Debe activar el fallback y devolver 503 si el servicio de Subastas falla")
    void fallbackActivadoPorFalloDeSubastas() {
        PlaceBidCommand command = new PlaceBidCommand(1L, 2L, new BigDecimal("100"));
        String authUserId = "2";

        //  cliente falso que simule una caída de red
        when(auctionClient.getAuctionById(anyLong()))
                .thenThrow(new RuntimeException("Connection refused"));

        // Ejecutamos el caso de uso y atrapamos la excepción que devuelve
        DomainException exception = assertThrows(DomainException.class, () -> {
            placeBidUseCase.execute(command, authUserId);
        });

        // Comprobamos que el Circuit Breaker hizo su trabajo:
        // Atrapó el RuntimeException feo y lo convirtió en ErrorCode.SERVICE_UNAVAILABLE
        assertEquals(ErrorCode.SERVICE_UNAVAILABLE, exception.getErrorCode(), "El error debe ser SERVICE_UNAVAILABLE (503)");
        assertTrue(exception.getMessage().contains("saturado o fuera de línea"), "El mensaje debe ser el del fallback");

        System.out.println("Test de Circuit Breaker superado. Mensaje devuelto: " + exception.getMessage());
    }
}