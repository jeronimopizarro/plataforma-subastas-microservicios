package com.portafolio.wallet.domain.entity;

import com.portafolio.wallet.domain.enums.TransactionType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class Transaction {
    private final Long id;
    private final Long walletId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String reference; // Ej: "Puja en subasta #5"
    private final LocalDateTime timestamp;

    private Transaction(Long id, Long walletId, BigDecimal amount, TransactionType type, String reference, LocalDateTime timestamp) {
        this.id = id;
        this.walletId = walletId;
        this.amount = amount;
        this.type = type;
        this.reference = reference;
        this.timestamp = timestamp;
    }

    public static Transaction create(Long walletId, BigDecimal amount, TransactionType type, String reference) {
        return new Transaction(null, walletId, amount, type, reference, LocalDateTime.now());
    }

    public static Transaction restore(Long id, Long walletId, BigDecimal amount, TransactionType type, String reference, LocalDateTime timestamp) {
        return new Transaction(id, walletId, amount, type, reference, timestamp);
    }
}
