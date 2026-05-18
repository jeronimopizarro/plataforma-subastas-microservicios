package com.portafolio.wallet.infrastructure.mapper;

import com.portafolio.wallet.domain.entity.Transaction;
import com.portafolio.wallet.infrastructure.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionEntity toEntity(Transaction domain) {
        if (domain == null) return null;

        return TransactionEntity.builder()
                .id(domain.getId())
                .walletId(domain.getWalletId())
                .amount(domain.getAmount())
                .type(domain.getType())
                .reference(domain.getReference())
                .timestamp(domain.getTimestamp())
                .build();
    }

    public Transaction toDomain(TransactionEntity entity) {
        if (entity == null) return null;

        return Transaction.restore(
                entity.getId(),
                entity.getWalletId(),
                entity.getAmount(),
                entity.getType(),
                entity.getReference(),
                entity.getTimestamp()
        );
    }
}
