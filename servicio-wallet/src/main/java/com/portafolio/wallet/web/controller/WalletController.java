package com.portafolio.wallet.web.controller;

import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.application.usecase.*;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.web.dto.TransactionRequest;
import com.portafolio.wallet.web.dto.WalletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final AddFundsUseCase addFundsUseCase;
    private final HoldFundsUseCase holdFundsUseCase;
    private final ReleaseFundsUseCase releaseFundsUseCase;
    private final CommitTransactionUseCase commitTransactionUseCase;
    private final FindWalletByUserIdUseCase findWalletByUserIdUseCase;

    public WalletController(AddFundsUseCase addFundsUseCase,
                            HoldFundsUseCase holdFundsUseCase,
                            ReleaseFundsUseCase releaseFundsUseCase,
                            CommitTransactionUseCase commitTransactionUseCase,
                            FindWalletByUserIdUseCase findWalletByUserIdUseCase) {
        this.addFundsUseCase = addFundsUseCase;
        this.holdFundsUseCase = holdFundsUseCase;
        this.releaseFundsUseCase = releaseFundsUseCase;
        this.commitTransactionUseCase = commitTransactionUseCase;
        this.findWalletByUserIdUseCase = findWalletByUserIdUseCase;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWalletByUserId(@PathVariable Long userId) {
        Wallet wallet = findWalletByUserIdUseCase.execute(userId);
        return ResponseEntity.ok(mapToResponse(wallet));
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<Void> addFunds(@PathVariable Long userId, @RequestBody TransactionRequest request) {
        WalletTransactionCommand command = new WalletTransactionCommand(userId, request.amount(), request.reference());
        addFundsUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/hold")
    public ResponseEntity<Void> holdFunds(@PathVariable Long userId, @RequestBody TransactionRequest request) {
        WalletTransactionCommand command = new WalletTransactionCommand(userId, request.amount(), request.reference());
        holdFundsUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/release")
    public ResponseEntity<Void> releaseFunds(@PathVariable Long userId, @RequestBody TransactionRequest request) {
        WalletTransactionCommand command = new WalletTransactionCommand(userId, request.amount(), request.reference());
        releaseFundsUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/commit")
    public ResponseEntity<Void> commitTransaction(@PathVariable Long userId, @RequestBody TransactionRequest request) {
        WalletTransactionCommand command = new WalletTransactionCommand(userId, request.amount(), request.reference());
        commitTransactionUseCase.execute(command);
        return ResponseEntity.ok().build();
    }

    private WalletResponse mapToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getAvailableBalance(),
                wallet.getHeldFunds()
        );
    }
}