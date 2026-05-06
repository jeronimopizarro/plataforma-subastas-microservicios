package com.portafolio.subastas.domain.entity;

import com.portafolio.subastas.domain.exception.InvalidProductException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateNewProductSuccessfully() {
        // Act
        Product product = Product.createNew("Laptop", "Potente", "NUEVO", "url", 1L);

        // Assert
        assertNotNull(product);
        assertEquals("Laptop", product.getTitle());
        assertTrue(product.isActive(), "Un producto nuevo debe nacer en estado activo");
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        // Act & Assert
        InvalidProductException exception = assertThrows(InvalidProductException.class, () -> {
            Product.createNew("", "Desc", "NUEVO", "url", 1L);
        });
        assertEquals("El título del producto es obligatorio.", exception.getMessage());
    }

    @Test
    void shouldDeactivateProductSuccessfully() {
        // Arrange
        Product product = Product.createNew("Laptop", "Potente", "NUEVO", "url", 1L);

        // Act
        product.deactivate();

        // Assert
        assertFalse(product.isActive());
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveProduct() {
        // Arrange
        Product product = Product.createNew("Laptop", "Potente", "NUEVO", "url", 1L);
        product.deactivate(); // Lo desactivamos la primera vez

        // Act & Assert
        assertThrows(InvalidProductException.class, product::deactivate,
                "Debería fallar al intentar desactivar un producto que ya está inactivo");
    }
}
