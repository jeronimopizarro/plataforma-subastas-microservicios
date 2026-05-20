package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.repository.BidRepository;
import com.portafolio.bidding.infrastructure.client.AuctionFeignClient;
import com.portafolio.bidding.infrastructure.client.WalletFeignClient;
import com.portafolio.bidding.infrastructure.client.dto.AuctionResponse;
import com.portafolio.bidding.infrastructure.client.dto.TransactionRequest;
import com.portafolio.bidding.infrastructure.client.dto.UpdateBidRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceBidUseCaseTest {

    @Mock private AuctionFeignClient auctionClient;
    @Mock private WalletFeignClient walletClient;
    @Mock private BidRepository bidRepository;

    @InjectMocks private PlaceBidUseCase placeBidUseCase;

    @Test
    @DisplayName("Debería procesar la puja exitosamente sin ganador previo")
    void shouldProcessBidSuccessfullyWithoutPreviousWinner() {
        PlaceBidCommand command = new PlaceBidCommand(1L, 2L, new BigDecimal("500.00"));
        AuctionResponse mockAuction = new AuctionResponse(1L, 10L, 3L, new BigDecimal("100.00"),
                new BigDecimal("100.00"), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusDays(1), "ACTIVE", null);

        when(auctionClient.getAuctionById(1L)).thenReturn(mockAuction);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArguments()[0]);

        Bid result = placeBidUseCase.execute(command);

        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getAmount());

        verify(walletClient, times(1)).holdFunds(eq(2L), any(TransactionRequest.class));
        verify(auctionClient, times(1)).updateCurrentBid(eq(1L), any(UpdateBidRequest.class));
        // Verificamos que NO se devolvieron fondos a nadie (porque no había ganador previo)
        verify(walletClient, never()).releaseFunds(anyLong(), any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Debería procesar la puja y devolver fondos al ganador previo")
    void shouldProcessBidAndReleaseFundsToPreviousWinner() {
        PlaceBidCommand command = new PlaceBidCommand(1L, 3L, new BigDecimal("1000.00"));
        AuctionResponse mockAuction = new AuctionResponse(1L, 10L, 5L, new BigDecimal("100.00"),
                new BigDecimal("500.00"), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusDays(1), "ACTIVE", 2L); // 2L es el ganador previo

        when(auctionClient.getAuctionById(1L)).thenReturn(mockAuction);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArguments()[0]);

        placeBidUseCase.execute(command);

        verify(walletClient, times(1)).holdFunds(eq(3L), any(TransactionRequest.class));
        verify(auctionClient, times(1)).updateCurrentBid(eq(1L), any(UpdateBidRequest.class));
        // Verificamos si se le devolvieron los fondos al usuario 2L
        verify(walletClient, times(1)).releaseFunds(eq(2L), any(TransactionRequest.class));
    }

    @Test
    @DisplayName("Debería ejecutar rollback (devolver fondos) si falla la actualización del catálogo")
    void shouldExecuteRollbackIfCatalogUpdateFails() {
        PlaceBidCommand command = new PlaceBidCommand(1L, 2L, new BigDecimal("500.00"));
        AuctionResponse mockAuction = new AuctionResponse(1L, 10L, 3L, new BigDecimal("100.00"),
                new BigDecimal("100.00"), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusDays(1), "ACTIVE", null);

        when(auctionClient.getAuctionById(1L)).thenReturn(mockAuction);
        // Simulamos caída de red en el catálogo
        doThrow(new RuntimeException("Error de conexión")).when(auctionClient).updateCurrentBid(eq(1L), any());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> placeBidUseCase.execute(command));
        assertTrue(exception.getMessage().contains("Se devolvieron los fondos"));

        // Verificamos el flujo de compensación: Se retuvo y luego se devolvió al mismo usuario (2L)
        verify(walletClient, times(1)).holdFunds(eq(2L), any(TransactionRequest.class));
        verify(walletClient, times(1)).releaseFunds(eq(2L), any(TransactionRequest.class));
        verify(bidRepository, never()).save(any());
    }
}