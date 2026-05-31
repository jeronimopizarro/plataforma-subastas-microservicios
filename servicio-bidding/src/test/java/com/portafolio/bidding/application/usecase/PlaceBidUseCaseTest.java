package com.portafolio.bidding.application.usecase;

import com.portafolio.bidding.application.dto.PlaceBidCommand;
import com.portafolio.bidding.application.port.BidEventPublisher;
import com.portafolio.bidding.domain.entity.Bid;
import com.portafolio.bidding.domain.repository.BidRepository;
import com.portafolio.bidding.infrastructure.client.AuctionFeignClient;
import com.portafolio.bidding.infrastructure.client.UserFeignClient;
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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceBidUseCaseTest {

    @Mock private AuctionFeignClient auctionClient;
    @Mock private WalletFeignClient walletClient;
    @Mock private BidRepository bidRepository;
    @Mock private BidEventPublisher bidEventPublisher;
    @Mock private UserFeignClient userFeignClient;

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

        when(userFeignClient.getUserEmail(2L)).thenReturn(Map.of("email", "jeronimo@gmail.com"));

        // PASAMOS "2" PORQUE EL BIDDER ID ES 2L
        Bid result = placeBidUseCase.execute(command, "2");

        assertNotNull(result);
        assertEquals(new BigDecimal("500.00"), result.getAmount());

        verify(walletClient, times(1)).holdFunds(eq(2L), any(TransactionRequest.class));
        verify(auctionClient, times(1)).updateCurrentBid(eq(1L), any(UpdateBidRequest.class));
        verify(walletClient, never()).releaseFunds(anyLong(), any(TransactionRequest.class));

        // comprobando además que la lógica de enmascaramiento funcionó ("jeronimo" -> "jer***").
        verify(bidEventPublisher, times(1)).publishNewBid(eq(1L), eq(2L), eq("jer***@gmail.com"), eq(new BigDecimal("500.00")));
    }

    @Test
    @DisplayName("Debería procesar la puja y devolver fondos al ganador previo")
    void shouldProcessBidAndReleaseFundsToPreviousWinner() {
        PlaceBidCommand command = new PlaceBidCommand(1L, 3L, new BigDecimal("1000.00"));
        AuctionResponse mockAuction = new AuctionResponse(1L, 10L, 5L, new BigDecimal("100.00"),
                new BigDecimal("500.00"), LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusDays(1), "ACTIVE", 2L);

        when(auctionClient.getAuctionById(1L)).thenReturn(mockAuction);
        when(bidRepository.save(any(Bid.class))).thenAnswer(i -> i.getArguments()[0]);

        // PASAMOS "3" PORQUE EL BIDDER ID ES 3L
        placeBidUseCase.execute(command, "3");

        verify(walletClient, times(1)).holdFunds(eq(3L), any(TransactionRequest.class));
        verify(auctionClient, times(1)).updateCurrentBid(eq(1L), any(UpdateBidRequest.class));
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
        doThrow(new RuntimeException("Error de conexión")).when(auctionClient).updateCurrentBid(eq(1L), any());

        // PASAMOS "2"
        RuntimeException exception = assertThrows(RuntimeException.class, () -> placeBidUseCase.execute(command, "2"));
        assertTrue(exception.getMessage().contains("Se devolvieron los fondos"));

        verify(walletClient, times(1)).holdFunds(eq(2L), any(TransactionRequest.class));
        verify(walletClient, times(1)).releaseFunds(eq(2L), any(TransactionRequest.class));
        verify(bidRepository, never()).save(any());

        // NUEVO: Agregamos anyString() como tercer parámetro para cumplir la firma de 4 argumentos
        verify(bidEventPublisher, never()).publishNewBid(anyLong(), anyLong(), anyString(), any(BigDecimal.class));
    }

    // ¡NUEVO TEST DE SEGURIDAD!
    @Test
    @DisplayName("Debería lanzar excepción si el usuario intenta pujar a nombre de otro")
    void shouldThrowExceptionWhenBiddingForAnotherUser() {
        // Intenta pujar a nombre del usuario 2L
        PlaceBidCommand command = new PlaceBidCommand(1L, 2L, new BigDecimal("500.00"));

        // Pero el token le pertenece al usuario "99"
        String hackerAuthId = "99";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> placeBidUseCase.execute(command, hackerAuthId));

        assertTrue(exception.getMessage().contains("No puedes realizar una puja a nombre de otro usuario"));

        // Verificamos que nada más se haya ejecutado
        verify(auctionClient, never()).getAuctionById(anyLong());
        verify(walletClient, never()).holdFunds(anyLong(), any());
    }
}