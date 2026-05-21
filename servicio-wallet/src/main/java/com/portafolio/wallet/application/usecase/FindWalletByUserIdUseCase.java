package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.UnauthorizedAccessException;
import com.portafolio.wallet.domain.exception.WalletNotFoundException;
import com.portafolio.wallet.domain.repository.WalletRepository;
import com.portafolio.wallet.web.dto.WalletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindWalletByUserIdUseCase {

    private final WalletRepository walletRepository;

    public FindWalletByUserIdUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public Wallet execute(Long userId, String authUserId) {

        validateSameOwner(userId, authUserId);

        return findWalletByUserId(userId);
    }

    private void validateSameOwner(Long userId, String authUserId) {
        if (!userId.toString().equals(authUserId)) {
            throw new UnauthorizedAccessException("Error 403: No tienes permisos para acceder a esta billetera.");
        }
    }

    private Wallet findWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("No se encontró una billetera para el usuario especificado."));
    }
}