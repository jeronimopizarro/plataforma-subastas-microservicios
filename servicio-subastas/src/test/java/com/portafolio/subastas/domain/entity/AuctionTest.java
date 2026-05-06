package com.portafolio.subastas.domain.entity;

import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.exception.InvalidAuctionException;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuctionTest {

    @Test
    void shouldCreateNewAuctionInDraftStatus() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        // Act
        Auction auction = Auction.createNew(1L, 2L, new BigDecimal("100"), start, end);

        // Assert
        assertNotNull(auction);
        assertEquals(AuctionStatus.DRAFT, auction.getStatus());
        assertEquals(new BigDecimal("100"), auction.getCurrentHighestBid(),
                "La puja más alta inicial debe ser igual al precio base");
    }

    @Test
    void shouldThrowExceptionWhenStartingPriceIsZeroOrNegative() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        // Act & Assert
        assertThrows(InvalidAuctionException.class, () -> {
            Auction.createNew(1L, 2L, BigDecimal.ZERO, start, end);
        });
    }

    @Test
    void shouldThrowExceptionWhenEndTimeIsBeforeStartTime() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1); // Fecha de fin anterior a la de inicio

        // Act & Assert
        assertThrows(InvalidAuctionException.class, () -> {
            Auction.createNew(1L, 2L, new BigDecimal("100"), start, end);
        });
    }

    @Test
    void shouldStartAuctionSuccessfully() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Auction auction = Auction.createNew(1L, 2L, new BigDecimal("100"), start, end);

        // Act
        auction.start();

        // Assert
        assertEquals(AuctionStatus.ACTIVE, auction.getStatus());
    }

    @Test
    void shouldFinishActiveAuctionAndSetWinner() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        Auction auction = Auction.createNew(1L, 2L, new BigDecimal("100"), start, end);
        auction.start(); // Estado = ACTIVE

        // Act
        auction.finish(5L, new BigDecimal("500"));

        // Assert
        assertEquals(AuctionStatus.FINISHED, auction.getStatus());
        assertEquals(5L, auction.getWinnerId());
        assertEquals(new BigDecimal("500"), auction.getCurrentHighestBid());
    }

    @Test
    void shouldThrowExceptionWhenFinishingDraftAuction() {
        // Arrange
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Auction auction = Auction.createNew(1L, 2L, new BigDecimal("100"), start, end);
        // Sigue en DRAFT

        // Act & Assert
        assertThrows(InvalidAuctionStateException.class, () -> {
            auction.finish(5L, new BigDecimal("500"));
        }, "No se debería poder finalizar una subasta que nunca inició");
    }
}
