package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.WalletNotFoundException;
import com.portafolio.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindWalletByUserIdUseCase {

    private final WalletRepository walletRepository;

    public FindWalletByUserIdUseCase(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public Wallet execute(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("No se encontró una billetera para el usuario especificado."));
    }
}
