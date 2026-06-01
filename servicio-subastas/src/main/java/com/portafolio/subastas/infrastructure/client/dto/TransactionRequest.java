package com.portafolio.subastas.infrastructure.client.dto;

import java.math.BigDecimal;

public record TransactionRequest(BigDecimal amount, String reference) {
}