package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.domain.entity.Transaction;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.enums.TransactionType;
import com.portafolio.wallet.domain.exception.WalletNotFoundException;
import com.portafolio.wallet.domain.repository.TransactionRepository;
import com.portafolio.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReleaseFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public ReleaseFundsUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void execute(WalletTransactionCommand command) {
        Wallet wallet = ensureWalletExists(command.userId());

        wallet.releaseFunds(command.amount());

        Transaction transaction = Transaction.create(wallet.getId(), command.amount(), TransactionType.RELEASE, command.reference());

        walletRepository.save(wallet);
        transactionRepository.save(transaction);
    }

    private Wallet ensureWalletExists(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException("No se encontró una billetera para el usuario especificado."));
    }
}
