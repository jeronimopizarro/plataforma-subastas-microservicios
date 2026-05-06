package com.portafolio.subastas.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.application.usecase.CancelAuctionUseCase;
import com.portafolio.subastas.application.usecase.CreateAuctionUseCase;
import com.portafolio.subastas.application.usecase.FindAuctionByIdUseCase;
import com.portafolio.subastas.application.usecase.ListAuctionsByStatusUseCase;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.enums.AuctionStatus;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuctionController.class)
class AuctionControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private CreateAuctionUseCase createAuctionUseCase;
    @MockitoBean private FindAuctionByIdUseCase findAuctionByIdUseCase;
    @MockitoBean private ListAuctionsByStatusUseCase listAuctionsByStatusUseCase;
    @MockitoBean private CancelAuctionUseCase cancelAuctionUseCase;

    @Test
    @DisplayName("POST /auctions - Debería retornar 201 Created")
    void shouldCreateAuction() throws Exception {
        // Arrange
        CreateAuctionCommand command = new CreateAuctionCommand(
                1L, 2L, new BigDecimal("100.00"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Auction mockAuction = Auction.restore(10L, 1L, 2L, new BigDecimal("100.00"),
                new BigDecimal("100.00"), command.startTime(), command.endTime(),
                AuctionStatus.DRAFT, null);

        when(createAuctionUseCase.execute(any(CreateAuctionCommand.class))).thenReturn(mockAuction);

        // Act & Assert
        mockMvc.perform(post("/auctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.productId").value(1L))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    @DisplayName("GET /auctions/{id} - Debería retornar 200 OK y la subasta")
    void shouldGetAuctionById() throws Exception {
        // Arrange
        Auction mockAuction = Auction.restore(1L, 10L, 20L, new BigDecimal("150.00"),
                new BigDecimal("200.00"), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), AuctionStatus.ACTIVE, null);

        when(findAuctionByIdUseCase.execute(1L)).thenReturn(mockAuction);

        // Act & Assert
        mockMvc.perform(get("/auctions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /auctions?status=ACTIVE - Debería retornar 200 OK y la lista")
    void shouldGetAuctionsByStatus() throws Exception {
        // Arrange
        Auction mockAuction = Auction.restore(1L, 10L, 20L, new BigDecimal("150.00"),
                new BigDecimal("200.00"), LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), AuctionStatus.ACTIVE, null);

        when(listAuctionsByStatusUseCase.execute(AuctionStatus.ACTIVE)).thenReturn(List.of(mockAuction));

        // Act & Assert
        mockMvc.perform(get("/auctions").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("PATCH /auctions/{id}/cancel - Debería retornar 204 No Content")
    void shouldCancelAuction() throws Exception {
        // Arrange
        doNothing().when(cancelAuctionUseCase).execute(1L);

        // Act & Assert
        mockMvc.perform(patch("/auctions/1/cancel"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /auctions - Debería retornar 409 Conflict si el producto ya está en subasta")
    void shouldReturn409WhenProductAlreadyInAuction() throws Exception {
        // Arrange
        CreateAuctionCommand command = new CreateAuctionCommand(
                1L, 2L, new BigDecimal("100.00"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        when(createAuctionUseCase.execute(any(CreateAuctionCommand.class)))
                .thenThrow(new InvalidAuctionStateException("El producto ya se encuentra en una subasta DRAFT o ACTIVE."));

        // Act & Assert
        mockMvc.perform(post("/auctions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("El producto ya se encuentra en una subasta DRAFT o ACTIVE."));
    }
}