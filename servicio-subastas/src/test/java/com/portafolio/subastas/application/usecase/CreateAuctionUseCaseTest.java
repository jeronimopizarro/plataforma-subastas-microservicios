package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateAuctionCommand;
import com.portafolio.subastas.domain.entity.Auction;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.InvalidAuctionStateException;
import com.portafolio.subastas.domain.repository.AuctionRepository;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAuctionUseCaseTest {

    @Mock
    private AuctionRepository auctionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateAuctionUseCase createAuctionUseCase;

    @Test
    void shouldCreateAuctionSuccessfully() {
        // 1. Arrange
        Long productId = 1L;
        CreateAuctionCommand command = new CreateAuctionCommand(
                productId, 2L, new BigDecimal("100"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Product activeProduct = Product.restore(productId, "Test", "Desc", "NUEVO", null, 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
        when(auctionRepository.existsActiveAuctionForProduct(productId)).thenReturn(false);
        when(auctionRepository.save(any(Auction.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act
        Auction result = createAuctionUseCase.execute(command);

        // 3. Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        verify(auctionRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenProductAlreadyInAuction() {
        // 1. Arrange
        Long productId = 1L;
        CreateAuctionCommand command = new CreateAuctionCommand(
                productId, 2L, new BigDecimal("100"),
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)
        );

        Product activeProduct = Product.restore(productId, "Test", "Desc", "NUEVO", null, 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));
        when(auctionRepository.existsActiveAuctionForProduct(productId)).thenReturn(true);

        // 2. Act & 3. Assert
        assertThrows(InvalidAuctionStateException.class, () -> createAuctionUseCase.execute(command));
        verify(auctionRepository, never()).save(any());
    }
}
