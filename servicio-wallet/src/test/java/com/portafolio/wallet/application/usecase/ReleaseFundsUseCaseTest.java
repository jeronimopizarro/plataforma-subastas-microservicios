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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReleaseFundsUseCaseTest {

    @Mock private WalletRepository walletRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private ReleaseFundsUseCase releaseFundsUseCase;

    @Test
    @DisplayName("Debería liberar fondos retenidos y guardarlos como saldo disponible")
    void shouldReleaseHeldFunds() {
        WalletTransactionCommand command = new WalletTransactionCommand(1L, new BigDecimal("200.00"), "Subasta Cancelada");
        // Tiene 500 disponibles y 200 retenidos
        Wallet mockWallet = Wallet.restore(10L, 1L, new BigDecimal("500.00"), new BigDecimal("200.00"));

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(mockWallet));

        releaseFundsUseCase.execute(command);

        // Ahora debería tener 700 disponibles y 0 retenidos
        assertEquals(new BigDecimal("700.00"), mockWallet.getAvailableBalance());
        assertEquals(new BigDecimal("0.00"), mockWallet.getHeldFunds());

        verify(walletRepository, times(1)).save(mockWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debería lanzar excepción si la billetera no existe")
    void shouldThrowExceptionWhenWalletNotFound() {
        WalletTransactionCommand command = new WalletTransactionCommand(99L, new BigDecimal("200.00"), "Ref");
        when(walletRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> releaseFundsUseCase.execute(command));
    }
}