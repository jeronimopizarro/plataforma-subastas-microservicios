package com.portafolio.wallet.domain.entity;

import com.portafolio.wallet.domain.exception.InsufficientFundsException;
import com.portafolio.wallet.domain.exception.InvalidWalletOperationException;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Wallet {

    private final Long id;
    private final Long userId;
    private BigDecimal availableBalance;
    private BigDecimal heldFunds;

    private Wallet(Long id, Long userId, BigDecimal availableBalance, BigDecimal heldFunds) {
        this.id = id;
        this.userId = userId;
        this.availableBalance = availableBalance != null ? availableBalance : BigDecimal.ZERO;
        this.heldFunds = heldFunds != null ? heldFunds : BigDecimal.ZERO;
    }

    public static Wallet createNew(Long userId) {
        if (userId == null) throw new InvalidWalletOperationException("El ID de usuario es obligatorio");
        return new Wallet(null, userId, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static Wallet restore(Long id, Long userId, BigDecimal availableBalance, BigDecimal heldFunds) {
        return new Wallet(id, userId, availableBalance, heldFunds);
    }

    public void addFunds(BigDecimal amount) {
        validateAmount(amount);
        this.availableBalance = this.availableBalance.add(amount);
    }

    public void holdFunds(BigDecimal amount) {
        validateAmount(amount);
        if (this.availableBalance.compareTo(amount) < 0) {
            throw new InsufficientFundsException("Saldo insuficiente para retener los fondos requeridos.");
        }
        this.availableBalance = this.availableBalance.subtract(amount);
        this.heldFunds = this.heldFunds.add(amount);
    }

    public void releaseFunds(BigDecimal amount) {
        validateAmount(amount);
        if (this.heldFunds.compareTo(amount) < 0) {
            throw new InvalidWalletOperationException("No se puede liberar un monto mayor al retenido.");
        }
        this.heldFunds = this.heldFunds.subtract(amount);
        this.availableBalance = this.availableBalance.add(amount);
    }

    public void commitFunds(BigDecimal amount) {
        validateAmount(amount);
        if (this.heldFunds.compareTo(amount) < 0) {
            throw new InvalidWalletOperationException("No se pueden descontar fondos que no han sido retenidos previamente.");
        }
        // Simplemente desaparecen de heldFunds, consumando el pago.
        this.heldFunds = this.heldFunds.subtract(amount);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWalletOperationException("El monto de la operación debe ser mayor a cero.");
        }
    }
}
