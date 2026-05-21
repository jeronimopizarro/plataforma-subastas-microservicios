package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAuctionUseCaseTest {

    @Mock private AuctionRepository auctionRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private CreateAuctionUseCase createAuctionUseCase;

    @Test
    @DisplayName("Debería crear subasta exitosamente si el usuario es dueño del producto")
    void shouldCreateAuctionSuccessfully() {
        Long productId = 1L;
        String authUserId = "2"; // El usuario dueño del producto

        CreateAuctionCommand command = new CreateAuctionCommand(
                productId, 2L, new BigDecimal("100"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        // El producto tiene sellerId 2L
        Product activeProduct = Product.restore(productId, "Test", "Desc", "NUEVO", null, 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
        when(auctionRepository.existsActiveAuctionForProduct(productId)).thenReturn(false);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act: Pasamos el authUserId
        Auction result = createAuctionUseCase.execute(command, authUserId);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        verify(auctionRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el producto ya está en subasta")
    void shouldThrowExceptionWhenProductAlreadyInAuction() {
        Long productId = 1L;
        String authUserId = "2";

        CreateAuctionCommand command = new CreateAuctionCommand(
                productId, 2L, new BigDecimal("100"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Product activeProduct = Product.restore(productId, "Test", "Desc", "NUEVO", null, 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
        when(auctionRepository.existsActiveAuctionForProduct(productId)).thenReturn(true);

        assertThrows(InvalidAuctionStateException.class, () -> createAuctionUseCase.execute(command, authUserId));
        verify(auctionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedAccessException si el usuario NO es dueño del producto")
    void shouldThrowExceptionWhenUserIsNotProductOwner() {
        Long productId = 1L;
        String hackerAuthUserId = "99"; // Usuario malintencionado

        CreateAuctionCommand command = new CreateAuctionCommand(
                productId, 2L, new BigDecimal("100"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        // El producto le pertenece al usuario 2L
        Product activeProduct = Product.restore(productId, "Test", "Desc", "NUEVO", null, 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

        assertThrows(UnauthorizedAccessException.class, () -> createAuctionUseCase.execute(command, hackerAuthUserId));
        verify(auctionRepository, never()).save(any());
    }
}