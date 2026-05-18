package com.portafolio.wallet.infrastructure.adapter;

import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.repository.WalletRepository;
import com.portafolio.wallet.infrastructure.entity.WalletEntity;
import com.portafolio.wallet.infrastructure.mapper.WalletMapper;
import com.portafolio.wallet.infrastructure.repository.JpaWalletRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WalletRepositoryAdapter implements WalletRepository {

    private final JpaWalletRepository jpaRepository;
    private final WalletMapper walletMapper;

    public WalletRepositoryAdapter(JpaWalletRepository jpaRepository, WalletMapper walletMapper) {
        this.jpaRepository = jpaRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public Wallet save(Wallet wallet) {
        WalletEntity entity = walletMapper.toEntity(wallet);
        WalletEntity savedEntity = jpaRepository.save(entity);
        return walletMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return jpaRepository.findById(id).map(walletMapper::toDomain);
    }

    @Override
    public Optional<Wallet> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(walletMapper::toDomain);
    }
}
