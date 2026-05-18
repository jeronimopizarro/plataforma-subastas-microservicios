package com.portafolio.wallet.domain.repository;

import com.portafolio.wallet.domain.entity.Transaction;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findByWalletId(Long walletId);
}
