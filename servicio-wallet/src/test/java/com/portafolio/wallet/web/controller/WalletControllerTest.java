package com.portafolio.wallet.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.wallet.application.dto.WalletTransactionCommand;
import com.portafolio.wallet.application.usecase.*;
import com.portafolio.wallet.domain.entity.Wallet;
import com.portafolio.wallet.domain.exception.InsufficientFundsException;
import com.portafolio.wallet.domain.exception.UnauthorizedAccessException;
import com.portafolio.wallet.web.dto.TransactionRequest;
import com.portafolio.wallet.web.dto.WalletResponse;
import com.portafolio.wallet.web.mapper.WalletResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@Import(WalletResponseMapper.class)
class WalletControllerTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean private AddFundsUseCase addFundsUseCase;
    @MockitoBean private HoldFundsUseCase holdFundsUseCase;
    @MockitoBean private ReleaseFundsUseCase releaseFundsUseCase;
    @MockitoBean private CommitTransactionUseCase commitTransactionUseCase;
    @MockitoBean private FindWalletByUserIdUseCase findWalletByUserIdUseCase;


    @Test
    @DisplayName("GET /wallets/{userId} - Debería retornar 200 OK y la billetera del usuario autenticado")
    void shouldGetWallet() throws Exception {
        // 1. Ahora el mock simula la respuesta real del Caso de Uso: la Entidad pura
        Wallet mockWallet = Wallet.restore(1L, 10L, new BigDecimal("1000.00"), BigDecimal.ZERO);

        when(findWalletByUserIdUseCase.execute(10L, "10")).thenReturn(mockWallet);

        // 2. Ejecutamos la petición. El controlador internamente usará el Spy del Mapper.
        mockMvc.perform(get("/wallets/10")
                        .header("X-User-Id", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10L))
                .andExpect(jsonPath("$.availableBalance").value(1000.00));
    }

    @Test
    @DisplayName("GET /wallets/{userId} - Debería retornar 403 Forbidden si consulta una billetera ajena")
    void shouldReturn403WhenAccessingOtherUserWallet() throws Exception {

        // Si el Usuario 1 pide la billetera 2, el Caso de Uso explota
        when(findWalletByUserIdUseCase.execute(2L, "1"))
                .thenThrow(new UnauthorizedAccessException("Error 403: No tienes permisos para acceder a esta billetera."));

        // El Usuario 1 intenta entrar a la ruta /wallets/2
        mockMvc.perform(get("/wallets/2")
                        .header("X-User-Id", "1"))
                .andExpect(status().isForbidden()); // Esperamos que lo rebote con un 403
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

    @Test
    @DisplayName("POST /wallets/{userId}/deposit - Debería retornar 200 OK al depositar siendo dueño")
    void shouldAddFundsWhenOwner() throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal("150.00"), "Recarga");

        // No hace nada porque retorna void
        doNothing().when(addFundsUseCase).execute(any(WalletTransactionCommand.class), eq("10"));

        mockMvc.perform(post("/wallets/10/deposit")
                        .header("X-User-Id", "10") // Dueño correcto
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /wallets/{userId}/deposit - Debería retornar 403 al depositar siendo intruso")
    void shouldReturn403WhenDepositingAsIntruder() throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal("150.00"), "Recarga");

        doThrow(new UnauthorizedAccessException("Error 403"))
                .when(addFundsUseCase).execute(any(WalletTransactionCommand.class), eq("1"));

        mockMvc.perform(post("/wallets/2/deposit")
                        .header("X-User-Id", "1") // Intruso
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /wallets/{userId}/release - Debería retornar 200 OK al liberar fondos (SAGA)")
    void shouldReleaseFundsSaga() throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal("200.00"), "Subasta Cancelada");
        doNothing().when(releaseFundsUseCase).execute(any(WalletTransactionCommand.class));

        mockMvc.perform(post("/wallets/10/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /wallets/{userId}/commit - Debería retornar 200 OK al confirmar pago (SAGA)")
    void shouldCommitTransactionSaga() throws Exception {
        TransactionRequest request = new TransactionRequest(new BigDecimal("300.00"), "Subasta Ganada");
        doNothing().when(commitTransactionUseCase).execute(any(WalletTransactionCommand.class));

        mockMvc.perform(post("/wallets/10/commit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}