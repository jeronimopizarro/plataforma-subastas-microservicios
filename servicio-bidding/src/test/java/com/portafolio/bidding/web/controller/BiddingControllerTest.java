package com.portafolio.bidding.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.usecase.PlaceBidUseCase;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.web.dto.BidRequest;
import com.portafolio.bidding.web.mapper.BidResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BiddingController.class)
@Import(BidResponseMapper.class)
class BiddingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private PlaceBidUseCase placeBidUseCase;

    @Test
    @DisplayName("POST /bids - Debería retornar 201 Created al procesar la puja")
    void shouldPlaceBidAndReturn201() throws Exception {
        BidRequest request = new BidRequest(1L, 2L, new BigDecimal("500.00"));

        // Simulamos la puja generada internamente
        Bid mockBid = Bid.restore(100L, 1L, 2L, new BigDecimal("500.00"), java.time.LocalDateTime.now());

        // 2. Le pasamos el parámetro de seguridad (el ID "2" como String)
        when(placeBidUseCase.execute(any(PlaceBidCommand.class), eq("2"))).thenReturn(mockBid);

        mockMvc.perform(post("/bids")
                        .header("X-User-Id", "2") // <-- 3. Inyectamos la cabecera
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.auctionId").value(1L))
                .andExpect(jsonPath("$.amount").value(500.00));
    }
}