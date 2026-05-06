package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeactivateProductUseCase deactivateProductUseCase;

    @Test
    void shouldDeactivateProductSuccessfully() {
        // Arrange
        Long productId = 1L;
        Product activeProduct = Product.restore(productId, "TV", "Desc", "NUEVO", "url", 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

        // Act
        deactivateProductUseCase.execute(productId);

        // Assert
        assertFalse(activeProduct.isActive());
        verify(productRepository, times(1)).save(activeProduct);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Arrange
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> deactivateProductUseCase.execute(productId));
        verify(productRepository, never()).save(any());
    }
}