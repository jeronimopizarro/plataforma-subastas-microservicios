package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.domain.entity.Transaction;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.WalletNotFoundException;
import com.portafolio.wallet.domain.repository.TransactionRepository;
import com.portafolio.wallet.domain.repository.WalletRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HoldFundsUseCaseTest {

    @Mock private WalletRepository walletRepository;
    @Mock private TransactionRepository transactionRepository;

    @InjectMocks private HoldFundsUseCase holdFundsUseCase;

    @Test
    @DisplayName("Debería congelar fondos y guardar la transacción")
    void shouldHoldFundsAndSaveTransaction() {
        // Arrange
        Long userId = 10L;
        WalletTransactionCommand command = new WalletTransactionCommand(userId, new BigDecimal("300.00"), "Puja #1");
        Wallet mockWallet = Wallet.restore(1L, userId, new BigDecimal("1000.00"), BigDecimal.ZERO);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        holdFundsUseCase.execute(command);

        assertEquals(new BigDecimal("700.00"), mockWallet.getAvailableBalance());
        assertEquals(new BigDecimal("300.00"), mockWallet.getHeldFunds());

        verify(walletRepository, times(1)).save(mockWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debería lanzar WalletNotFoundException si el usuario no tiene billetera")
    void shouldThrowExceptionWhenWalletNotFound() {
        Long userId = 99L;
        WalletTransactionCommand command = new WalletTransactionCommand(userId, new BigDecimal("300.00"), "Puja #1");

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> holdFundsUseCase.execute(command));

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }
}