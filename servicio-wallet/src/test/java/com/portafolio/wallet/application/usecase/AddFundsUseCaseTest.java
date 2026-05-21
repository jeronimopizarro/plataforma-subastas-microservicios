package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.domain.entity.Transaction;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.UnauthorizedAccessException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddFundsUseCaseTest {

    @Mock private WalletRepository walletRepository;
    @Mock private TransactionRepository transactionRepository;
    @InjectMocks private AddFundsUseCase addFundsUseCase;

    @Test
    @DisplayName("Debería agregar fondos si el usuario es el dueño y la billetera ya existe")
    void shouldAddFundsWhenUserIsOwnerAndWalletExists() {
        Long userId = 1L;
        String authUserId = "1";
        WalletTransactionCommand command = new WalletTransactionCommand(userId, new BigDecimal("100.00"), "MercadoPago");
        Wallet mockWallet = Wallet.restore(10L, userId, new BigDecimal("50.00"), BigDecimal.ZERO);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        addFundsUseCase.execute(command, authUserId);

        assertEquals(new BigDecimal("150.00"), mockWallet.getAvailableBalance());
        verify(walletRepository, times(1)).save(mockWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedAccessException si intenta depositar en cuenta ajena")
    void shouldThrowUnauthorizedWhenDepositingToOtherWallet() {
        WalletTransactionCommand command = new WalletTransactionCommand(2L, new BigDecimal("100.00"), "Ref");

        assertThrows(UnauthorizedAccessException.class, () -> {
            addFundsUseCase.execute(command, "1"); // User 1 deposita en Wallet 2
        });

        verify(walletRepository, never()).save(any());
    }
}