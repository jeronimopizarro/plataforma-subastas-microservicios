package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.UnauthorizedAccessException;
import com.portafolio.wallet.domain.exception.WalletNotFoundException;
import com.portafolio.wallet.domain.repository.WalletRepository;
import com.portafolio.wallet.web.dto.WalletResponse;
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
class FindWalletByUserIdUseCaseTest {

    @Mock private WalletRepository walletRepository;
    @InjectMocks private FindWalletByUserIdUseCase findWalletByUserIdUseCase;

    @Test
    @DisplayName("Debería retornar la Entidad Wallet si el usuario es el dueño y la billetera existe")
    void shouldReturnWalletWhenUserIsOwner() {
        Long userId = 1L;
        String authUserId = "1";
        Wallet mockWallet = Wallet.restore(100L, userId, new BigDecimal("500.00"), BigDecimal.ZERO);

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(mockWallet));

        Wallet resultWallet = findWalletByUserIdUseCase.execute(userId, authUserId);

        assertNotNull(resultWallet);
        assertEquals(userId, resultWallet.getUserId());
        assertEquals(new BigDecimal("500.00"), resultWallet.getAvailableBalance());
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedAccessException si un intruso intenta ver la billetera")
    void shouldThrowUnauthorizedWhenUserIsNotOwner() {
        assertThrows(UnauthorizedAccessException.class, () -> {
            findWalletByUserIdUseCase.execute(2L, "1"); // ID 1 pide ver ID 2
        });

        // Verificamos que la BD nunca fue consultada (Falla rápido)
        verify(walletRepository, never()).findByUserId(anyLong());
    }

    @Test
    @DisplayName("Debería lanzar WalletNotFoundException si la billetera no existe")
    void shouldThrowNotFoundWhenWalletDoesNotExist() {
        Long userId = 1L;
        String authUserId = "1";

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> {
            findWalletByUserIdUseCase.execute(userId, authUserId);
        });
    }
}