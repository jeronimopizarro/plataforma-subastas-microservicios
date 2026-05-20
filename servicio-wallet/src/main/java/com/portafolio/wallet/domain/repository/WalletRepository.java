package com.portafolio.wallet.domain.repository;

import com.portafolio.wallet.domain.entity.Wallet;

import java.util.Optional;

public interface WalletRepository {

    Wallet save(Wallet wallet);

    Optional<Wallet> findById(Long id);

    Optional<Wallet> findByUserId(Long userId);
}
