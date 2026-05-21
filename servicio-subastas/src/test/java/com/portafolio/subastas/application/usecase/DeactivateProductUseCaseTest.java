package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Debería desactivar el producto si el usuario es el dueño")
    void shouldDeactivateProductSuccessfully() {
        Long productId = 1L;
        String authUserId = "2"; // Simulamos que el token pertenece al usuario 2

        // Creamos el producto y le asignamos el sellerId 2L (Coincide con el authUserId)
        Product activeProduct = Product.restore(productId, "TV", "Desc", "NUEVO", "url", 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

        deactivateProductUseCase.execute(productId, authUserId);

        assertFalse(activeProduct.isActive());
        verify(productRepository, times(1)).save(activeProduct);
    }

    @Test
    @DisplayName("Debería lanzar ProductNotFoundException si el producto no existe")
    void shouldThrowExceptionWhenProductNotFound() {
        Long productId = 99L;
        String authUserId = "2";

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> deactivateProductUseCase.execute(productId, authUserId));

        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar UnauthorizedAccessException si el usuario no es el dueño")
    void shouldThrowExceptionWhenUserIsNotOwner() {
        Long productId = 1L;
        String authUserId = "99"; // El usuario 99 intenta apagar el producto

        // Pero el producto le pertenece al usuario 2L
        Product activeProduct = Product.restore(productId, "TV", "Desc", "NUEVO", "url", 2L, true);

        when(productRepository.findById(productId)).thenReturn(Optional.of(activeProduct));

        // Verificamos que explote con nuestra excepción 403
        assertThrows(UnauthorizedAccessException.class, () -> deactivateProductUseCase.execute(productId, authUserId));

        // Verificamos que no se haya guardado nada por error
        verify(productRepository, never()).save(any());
    }
}