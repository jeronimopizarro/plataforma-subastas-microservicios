package com.portafolio.wallet.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.application.usecase.*;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.InsufficientFundsException;
import com.portafolio.wallet.web.dto.TransactionRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private AddFundsUseCase addFundsUseCase;
    @MockitoBean private HoldFundsUseCase holdFundsUseCase;
    @MockitoBean private ReleaseFundsUseCase releaseFundsUseCase;
    @MockitoBean private CommitTransactionUseCase commitTransactionUseCase;
    @MockitoBean private FindWalletByUserIdUseCase findWalletByUserIdUseCase;

    @Test
    @DisplayName("GET /wallets/{userId} - Debería retornar 200 OK y la billetera")
    void shouldGetWallet() throws Exception {
        Wallet mockWallet = Wallet.restore(1L, 10L, new BigDecimal("1000.00"), BigDecimal.ZERO);
        when(findWalletByUserIdUseCase.execute(10L)).thenReturn(mockWallet);

        mockMvc.perform(get("/wallets/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10L))
                .andExpect(jsonPath("$.availableBalance").value(1000.00));
    }

    @Test
    @DisplayName("POST /wallets/{userId}/hold - Debería retornar 200 OK al retener fondos")
    void shouldHoldFunds() throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal("300.00"), "Subasta #1");
        doNothing().when(holdFundsUseCase).execute(any(WalletTransactionCommand.class));

        mockMvc.perform(post("/wallets/10/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /wallets/{userId}/hold - Debería retornar 400 Bad Request si no hay fondos")
    void shouldReturn400WhenInsufficientFunds() throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal("9999.00"), "Subasta #1");

        doThrow(new InsufficientFundsException("Saldo insuficiente"))
                .when(holdFundsUseCase).execute(any(WalletTransactionCommand.class));

        mockMvc.perform(post("/wallets/10/hold")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente"));
    }
}