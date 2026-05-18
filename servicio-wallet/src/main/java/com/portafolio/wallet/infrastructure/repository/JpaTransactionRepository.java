package com.portafolio.wallet.infrastructure.repository;

import com.portafolio.wallet.infrastructure.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findByWalletId(Long walletId);
}
