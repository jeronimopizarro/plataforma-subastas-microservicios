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
class FinishAuctionsUseCaseTest {

    @Mock
    private AuctionRepository auctionRepository;

    private FinishAuctionsUseCase finishAuctionsUseCase;

    @BeforeEach
    void setUp() {
        AuctionStateProcessor stateProcessor = new AuctionStateProcessor(auctionRepository);
        finishAuctionsUseCase = new FinishAuctionsUseCase(auctionRepository, stateProcessor);
    }

    @Test
    void shouldFinishPendingAuctionsSuccessfully() {
        // 1. Arrange
        // Creamos dos subastas en estado ACTIVE con fechas de fin ya vencidas
        Auction auction1 = Auction.restore(1L, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("150"), LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusHours(1), AuctionStatus.ACTIVE, 5L);

        Auction auction2 = Auction.restore(2L, 11L, 21L, new BigDecimal("200"),
                new BigDecimal("300"), LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusHours(2), AuctionStatus.ACTIVE, 8L);

        when(auctionRepository.findAuctionsToFinish(any(LocalDateTime.class)))
                .thenReturn(List.of(auction1, auction2));

        // 2. Act
        finishAuctionsUseCase.execute();

        // 3. Assert
        // Verificamos que hayan pasado a FINISHED
        assertEquals(AuctionStatus.FINISHED, auction1.getStatus());
        assertEquals(AuctionStatus.FINISHED, auction2.getStatus());

        // Verificamos que conserven los ganadores actuales (por la lambda)
        assertEquals(5L, auction1.getWinnerId());
        assertEquals(8L, auction2.getWinnerId());

        // Verificamos el guardado
        verify(auctionRepository, times(1)).save(auction1);
        verify(auctionRepository, times(1)).save(auction2);
    }
}