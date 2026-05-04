package com.portafolio.subastas.web.controller;

import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.application.usecase.CreateProductUseCase;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.web.dto.ProductResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;

    public ProductController(CreateProductUseCase createProductUseCase) {
        this.createProductUseCase = createProductUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductCommand command) {
        Product savedProduct = createProductUseCase.execute(command);

        ProductResponse response = mapToResponse(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getCondition(),
                product.getImageUrl(),
                product.getSellerId(),
                product.isActive()
        );
    }
}
