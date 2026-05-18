package com.portafolio.wallet.domain.entity;

import com.portafolio.wallet.domain.exception.InsufficientFundsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    @DisplayName("Debería retener fondos correctamente si hay saldo suficiente")
    void shouldHoldFundsSuccessfully() {
        Wallet wallet = Wallet.restore(1L, 10L, new BigDecimal("1000.00"), BigDecimal.ZERO);

        wallet.holdFunds(new BigDecimal("300.00"));

        assertEquals(new BigDecimal("700.00"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("300.00"), wallet.getHeldFunds());
    }

    @Test
    @DisplayName("Debería lanzar InsufficientFundsException al intentar retener más del saldo disponible")
    void shouldThrowExceptionWhenHoldingMoreThanAvailable() {
        Wallet wallet = Wallet.restore(1L, 10L, new BigDecimal("100.00"), BigDecimal.ZERO);

        assertThrows(InsufficientFundsException.class, () -> {
            wallet.holdFunds(new BigDecimal("150.00"));
        });
    }

    @Test
    @DisplayName("Debería liberar fondos retenidos y devolverlos al saldo disponible")
    void shouldReleaseFundsSuccessfully() {
        Wallet wallet = Wallet.restore(1L, 10L, new BigDecimal("500.00"), new BigDecimal("200.00"));

        wallet.releaseFunds(new BigDecimal("200.00"));

        assertEquals(new BigDecimal("700.00"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("0.00"), wallet.getHeldFunds());
    }

    @Test
    @DisplayName("Debería consumar (commit) los fondos retenidos descontándolos definitivamente")
    void shouldCommitFundsSuccessfully() {
        Wallet wallet = Wallet.restore(1L, 10L, new BigDecimal("500.00"), new BigDecimal("200.00"));

        wallet.commitFunds(new BigDecimal("200.00"));

        // El balance disponible queda igual, pero los fondos retenidos desaparecen (se cobraron)
        assertEquals(new BigDecimal("500.00"), wallet.getAvailableBalance());
        assertEquals(new BigDecimal("0.00"), wallet.getHeldFunds());
    }
}