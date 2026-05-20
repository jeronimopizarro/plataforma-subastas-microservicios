package com.portafolio.bidding.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BidTest {

    @Test
    @DisplayName("Debería crear una nueva puja exitosamente")
    void shouldCreateNewBidSuccessfully() {
        Long auctionId = 1L;
        Long bidderId = 2L;
        BigDecimal amount = new BigDecimal("500.00");

        Bid bid = Bid.createNew(auctionId, bidderId, amount);

        assertNotNull(bid);
        assertEquals(auctionId, bid.getAuctionId());
        assertEquals(bidderId, bid.getBidderId());
        assertEquals(amount, bid.getAmount());
        assertNotNull(bid.getTimestamp());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el monto es cero o negativo")
    void shouldThrowExceptionWhenAmountIsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bid.createNew(1L, 2L, BigDecimal.ZERO);
        }, "El monto de la puja debe ser mayor a cero.");

        assertThrows(IllegalArgumentException.class, () -> {
            Bid.createNew(1L, 2L, new BigDecimal("-100"));
        }, "El monto de la puja debe ser mayor a cero.");
    }

    @Test
    @DisplayName("Debería lanzar excepción si faltan identificadores")
    void shouldThrowExceptionWhenIdsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            Bid.createNew(null, 2L, new BigDecimal("500"));
        });

        assertThrows(IllegalArgumentException.class, () -> {
            Bid.createNew(1L, null, new BigDecimal("500"));
        });
    }
}