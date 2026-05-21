package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.exception.AuctionNotFoundException;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelAuctionUseCaseTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private CancelAuctionUseCase cancelAuctionUseCase;

    @Test
    @DisplayName("Debería cancelar la subasta exitosamente si el usuario es el vendedor")
    void shouldCancelAuctionSuccessfully() {
        // 1. Arrange
        Long auctionId = 1L;
        String authUserId = "20"; // <-- COINCIDE CON EL SELLER ID

        Auction activeAuction = Auction.restore(auctionId, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("150"), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), AuctionStatus.ACTIVE, null);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(activeAuction));

        // 2. Act: Le pasamos el segundo parámetro
        cancelAuctionUseCase.execute(auctionId, authUserId);

        // 3. Assert
        assertEquals(AuctionStatus.CANCELLED, activeAuction.getStatus());
        verify(auctionRepository, times(1)).save(activeAuction);
    }

    @Test
    @DisplayName("Debería lanzar excepción si la subasta no existe")
    void shouldThrowExceptionWhenAuctionNotFound() {
        // 1. Arrange
        Long auctionId = 99L;
        String authUserId = "20";

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        assertThrows(AuctionNotFoundException.class, () -> cancelAuctionUseCase.execute(auctionId, authUserId));
        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si se intenta cancelar una subasta ya finalizada")
    void shouldThrowExceptionWhenCancellingFinishedAuction() {
        // 1. Arrange
        Long auctionId = 1L;
        String authUserId = "20"; // <-- COINCIDE PARA PASAR LA SEGURIDAD

        Auction finishedAuction = Auction.restore(auctionId, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("500"), LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), AuctionStatus.FINISHED, 5L);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(finishedAuction));

        // 2. Act & 3. Assert (Debe fallar por el estado, no por la seguridad)
        assertThrows(InvalidAuctionStateException.class, () -> cancelAuctionUseCase.execute(auctionId, authUserId));
        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedAccessException si el usuario no es el dueño de la subasta")
    void shouldThrowExceptionWhenUserIsNotSeller() {
        // 1. Arrange
        Long auctionId = 1L;
        String hackerAuthUserId = "99"; // <-- NO COINCIDE CON EL SELLER ID (20L)

        // La subasta le pertenece al usuario 20L
        Auction activeAuction = Auction.restore(auctionId, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("150"), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), AuctionStatus.ACTIVE, null);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(activeAuction));

        // 2. Act & 3. Assert
        assertThrows(UnauthorizedAccessException.class, () -> cancelAuctionUseCase.execute(auctionId, hackerAuthUserId));

        // Verificamos que la subasta jamás se canceló y no se guardó en BD
        verify(auctionRepository, never()).save(any());
    }
}