package com.portafolio.wallet.application.usecase;

import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.domain.entity.Transaction;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.enums.TransactionType;
import com.portafolio.wallet.domain.exception.UnauthorizedAccessException;
import com.portafolio.wallet.domain.repository.TransactionRepository;
import com.portafolio.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AddFundsUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void execute(WalletTransactionCommand command, String authUserId) {
        validateSameOwner(command, authUserId);
        Wallet wallet = ensureWalletExistsOrCreateNewOne(command.userId());

        wallet.addFunds(command.amount());

        Transaction transaction = Transaction.create(wallet.getId(), command.amount(), TransactionType.DEPOSIT, command.reference());

        walletRepository.save(wallet);
        transactionRepository.save(transaction);
    }

    private static void validateSameOwner(WalletTransactionCommand command, String authUserId) {
        if (!command.userId().toString().equals(authUserId)) {
            throw new UnauthorizedAccessException("Error 403: No tienes permisos para depositar en esta billetera.");
        }
    }

    private Wallet ensureWalletExistsOrCreateNewOne(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseGet(() -> walletRepository.save(Wallet.createNew(userId)));
    }
}
