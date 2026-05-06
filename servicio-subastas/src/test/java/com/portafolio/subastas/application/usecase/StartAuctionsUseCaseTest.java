package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.util.AuctionStateProcessor;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartAuctionsUseCaseTest {

    @Mock
    private AuctionRepository auctionRepository;

    private StartAuctionsUseCase startAuctionsUseCase;

    @BeforeEach
    void setUp() {
        // Usamos el procesador REAL, pero le inyectamos el repositorio FALSO (Mock)
        AuctionStateProcessor stateProcessor = new AuctionStateProcessor(auctionRepository);
        startAuctionsUseCase = new StartAuctionsUseCase(auctionRepository, stateProcessor);
    }

    @Test
    void shouldStartPendingAuctionsSuccessfully() {
        // 1. Arrange
        // Creamos dos subastas en estado DRAFT con fechas vencidas (listas para iniciar)
        Auction auction1 = Auction.restore(1L, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("100"), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusDays(1), AuctionStatus.DRAFT, null);

        Auction auction2 = Auction.restore(2L, 11L, 21L, new BigDecimal("200"),
                new BigDecimal("200"), LocalDateTime.now().minusHours(2),
                LocalDateTime.now().plusDays(1), AuctionStatus.DRAFT, null);

        when(auctionRepository.findAuctionsToStart(any(LocalDateTime.class)))
                .thenReturn(List.of(auction1, auction2));

        // 2. Act
        startAuctionsUseCase.execute();

        // 3. Assert
        // Verificamos que el procesador haya ejecutado 'start()' en ambas
        assertEquals(AuctionStatus.ACTIVE, auction1.getStatus());
        assertEquals(AuctionStatus.ACTIVE, auction2.getStatus());

        // Verificamos que se haya llamado a save() exactamente una vez por cada subasta
        verify(auctionRepository, times(1)).save(auction1);
        verify(auctionRepository, times(1)).save(auction2);
    }
}
