package com.portafolio.wallet.web.controller;

import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.application.usecase.*;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.web.dto.TransactionRequest;
import com.portafolio.wallet.web.dto.WalletResponse;
import com.portafolio.wallet.web.mapper.WalletResponseMapper;
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
    private final WalletResponseMapper walletResponseMapper;

    public WalletController(AddFundsUseCase addFundsUseCase,
                            HoldFundsUseCase holdFundsUseCase,
                            ReleaseFundsUseCase releaseFundsUseCase,
                            CommitTransactionUseCase commitTransactionUseCase,
                            FindWalletByUserIdUseCase findWalletByUserIdUseCase, WalletResponseMapper walletResponseMapper) {
        this.addFundsUseCase = addFundsUseCase;
        this.holdFundsUseCase = holdFundsUseCase;
        this.releaseFundsUseCase = releaseFundsUseCase;
        this.commitTransactionUseCase = commitTransactionUseCase;
        this.findWalletByUserIdUseCase = findWalletByUserIdUseCase;
        this.walletResponseMapper = walletResponseMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<WalletResponse> getMyWallet(
            @RequestHeader("X-User-Id") String authUserId) {

        Long userId = Long.valueOf(authUserId);
        Wallet wallet = findWalletByUserIdUseCase.execute(userId, authUserId);
        return ResponseEntity.ok(walletResponseMapper.toResponse(wallet));
    }

    @PostMapping("/me/deposit")
    public ResponseEntity<Void> addFunds(
            @RequestBody TransactionRequest request,
            @RequestHeader("X-User-Id") String authUserId) {

        Long userId = Long.valueOf(authUserId);
        WalletTransactionCommand command = new WalletTransactionCommand(userId, request.amount(), request.reference());
        addFundsUseCase.execute(command, authUserId);
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
}