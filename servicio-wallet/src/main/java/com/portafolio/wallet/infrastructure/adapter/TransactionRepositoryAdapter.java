package com.portafolio.wallet.infrastructure.adapter;

import com.portafolio.wallet.domain.entity.Transaction;
import com.portafolio.wallet.domain.repository.TransactionRepository;
import com.portafolio.wallet.infrastructure.entity.TransactionEntity;
import com.portafolio.wallet.infrastructure.mapper.TransactionMapper;
import com.portafolio.wallet.infrastructure.repository.JpaTransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final JpaTransactionRepository jpaRepository;
    private final TransactionMapper transactionMapper;

    public TransactionRepositoryAdapter(JpaTransactionRepository jpaRepository, TransactionMapper transactionMapper) {
        this.jpaRepository = jpaRepository;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = transactionMapper.toEntity(transaction);
        TransactionEntity savedEntity = jpaRepository.save(entity);
        return transactionMapper.toDomain(savedEntity);
    }

    @Override
    public List<Transaction> findByWalletId(Long walletId) {
        return jpaRepository.findByWalletId(walletId).stream()
                .map(transactionMapper::toDomain)
                .collect(Collectors.toList());
    }
}
