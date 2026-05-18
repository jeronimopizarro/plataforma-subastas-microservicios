package com.portafolio.wallet.infrastructure.mapper;

import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.infrastructure.entity.WalletEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletEntity toEntity(Wallet domain) {
        if (domain == null) return null;

        return WalletEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .availableBalance(domain.getAvailableBalance())
                .heldFunds(domain.getHeldFunds())
                .build();
    }

    public Wallet toDomain(WalletEntity entity) {
        if (entity == null) return null;

        return Wallet.restore(
                entity.getId(),
                entity.getUserId(),
                entity.getAvailableBalance(),
                entity.getHeldFunds()
        );
    }
}
