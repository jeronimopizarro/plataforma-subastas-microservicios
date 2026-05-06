package com.portafolio.subastas.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.application.usecase.CreateProductUseCase;
import com.portafolio.subastas.application.usecase.DeactivateProductUseCase;
import com.portafolio.subastas.application.usecase.FindProductByIdUseCase;
import com.portafolio.subastas.application.usecase.ListProductsUseCase;
import com.portafolio.subastas.domain.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // Inyectamos los mocks de los casos de uso para Spring Context
    @MockitoBean private CreateProductUseCase createProductUseCase;
    @MockitoBean private FindProductByIdUseCase findProductByIdUseCase;
    @MockitoBean private ListProductsUseCase listProductsUseCase;
    @MockitoBean private DeactivateProductUseCase deactivateProductUseCase;

    @Test
    @DisplayName("POST /products - Debería retornar 201 Created y el producto")
    void shouldCreateProduct() throws Exception {
        // Arrange
        CreateProductCommand command = new CreateProductCommand(
                "Silla Gamer", "Silla ergonómica negra", "NUEVO", "http://img.com/silla.jpg", 10L
        );

        // Simulamos el producto guardado (con ID asignado)
        Product mockProduct = Product.restore(
                1L, command.title(), command.description(), command.condition(),
                command.imageUrl(), command.sellerId(), true
        );

        when(createProductUseCase.execute(any(CreateProductCommand.class))).thenReturn(mockProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Silla Gamer"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @DisplayName("GET /products/{id} - Debería retornar 200 OK y el producto solicitado")
    void shouldGetProductById() throws Exception {
        // Arrange
        Product mockProduct = Product.restore(
                1L, "Monitor 4K", "Monitor 27 pulgadas", "USADO", "http://img.com/monitor.jpg", 10L, true
        );

        when(findProductByIdUseCase.execute(1L)).thenReturn(mockProduct);

        // Act & Assert
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Monitor 4K"));
    }

    @Test
    @DisplayName("GET /products - Debería retornar 200 OK y la lista de productos")
    void shouldGetAllProducts() throws Exception {
        // Arrange
        Product mockProduct = Product.restore(
                1L, "Teclado Mecánico", "Switches red", "NUEVO", "http://img.com/teclado.jpg", 5L, true
        );

        when(listProductsUseCase.execute()).thenReturn(List.of(mockProduct));

        // Act & Assert
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Teclado Mecánico"));
    }

    @Test
    @DisplayName("PATCH /products/{id}/deactivate - Debería retornar 204 No Content")
    void shouldDeactivateProduct() throws Exception {
        // Arrange
        doNothing().when(deactivateProductUseCase).execute(1L);

        // Act & Assert
        mockMvc.perform(patch("/products/1/deactivate"))
                .andExpect(status().isNoContent());
    }
}