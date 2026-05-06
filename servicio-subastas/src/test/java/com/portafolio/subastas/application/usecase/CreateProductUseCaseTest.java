package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CreateProductUseCase createProductUseCase;

    @Test
    void shouldCreateAndSaveProductSuccessfully() {
        // 1. Arrange
        CreateProductCommand command = new CreateProductCommand(
                "Silla Gamer",
                "Silla ergonómica negra",
                "NUEVO",
                "http://imagen.com/silla.jpg",
                10L
        );

        // Simulamos que al guardar, la base de datos nos devuelve el mismo producto pero ya con un ID generado (1L)
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            // Usamos el método restore para simular el producto saliendo de la BD con ID
            return Product.restore(
                    1L,
                    savedProduct.getTitle(),
                    savedProduct.getDescription(),
                    savedProduct.getCondition(),
                    savedProduct.getImageUrl(),
                    savedProduct.getSellerId(),
                    savedProduct.isActive()
            );
        });

        // 2. Act
        Product result = createProductUseCase.execute(command);

        // 3. Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Silla Gamer", result.getTitle());
        assertEquals(10L, result.getSellerId());

        verify(productRepository, times(1)).save(any(Product.class));
    }
}