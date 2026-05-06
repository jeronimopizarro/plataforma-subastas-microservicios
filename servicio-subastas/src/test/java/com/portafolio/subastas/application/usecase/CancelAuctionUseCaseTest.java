package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.exception.AuctionNotFoundException;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelAuctionUseCaseTest {

    @Mock
    private AuctionRepository auctionRepository;

    @InjectMocks
    private CancelAuctionUseCase cancelAuctionUseCase;

    @Test
    void shouldCancelAuctionSuccessfully() {
        // 1. Arrange
        Long auctionId = 1L;
        Auction activeAuction = Auction.restore(auctionId, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("150"), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), AuctionStatus.ACTIVE, null);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(activeAuction));

        // 2. Act
        cancelAuctionUseCase.execute(auctionId);

        // 3. Assert
        assertEquals(AuctionStatus.CANCELLED, activeAuction.getStatus());
        verify(auctionRepository, times(1)).save(activeAuction);
    }

    @Test
    void shouldThrowExceptionWhenAuctionNotFound() {
        // 1. Arrange
        Long auctionId = 99L;
        when(auctionRepository.findById(auctionId)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        assertThrows(AuctionNotFoundException.class, () -> cancelAuctionUseCase.execute(auctionId));
        verify(auctionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCancellingFinishedAuction() {
        // 1. Arrange
        Long auctionId = 1L;
        Auction finishedAuction = Auction.restore(auctionId, 10L, 20L, new BigDecimal("100"),
                new BigDecimal("500"), LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), AuctionStatus.FINISHED, 5L);

        when(auctionRepository.findById(auctionId)).thenReturn(Optional.of(finishedAuction));

        // 2. Act & 3. Assert
        assertThrows(InvalidAuctionStateException.class, () -> cancelAuctionUseCase.execute(auctionId));
        verify(auctionRepository, never()).save(any());
    }
}
