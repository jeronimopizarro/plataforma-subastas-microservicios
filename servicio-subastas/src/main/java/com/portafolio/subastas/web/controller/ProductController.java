package com.portafolio.subastas.web.controller;

import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.application.usecase.CreateProductUseCase;
import com.portafolio.subastas.application.usecase.DeactivateProductUseCase;
import com.portafolio.subastas.application.usecase.FindProductByIdUseCase;
import com.portafolio.subastas.application.usecase.ListProductsUseCase;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.web.dto.ProductResponse;
import com.portafolio.subastas.web.mapper.ProductResponseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateProductUseCase createProductUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final ListProductsUseCase listProductsUseCase;
    private final DeactivateProductUseCase deactivateProductUseCase;
    private final ProductResponseMapper productResponseMapper;

    public ProductController(CreateProductUseCase createProductUseCase,
                             FindProductByIdUseCase findProductByIdUseCase,
                             ListProductsUseCase listProductsUseCase,
                             DeactivateProductUseCase deactivateProductUseCase, ProductResponseMapper productResponseMapper) {
        this.createProductUseCase = createProductUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.listProductsUseCase = listProductsUseCase;
        this.deactivateProductUseCase = deactivateProductUseCase;
        this.productResponseMapper = productResponseMapper;
    }

    // REQUIERE AUTORIZACIÓN
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody CreateProductCommand command,
            @RequestHeader("X-User-Id") String authUserId) {

        Product savedProduct = createProductUseCase.execute(command, authUserId);

        ProductResponse response = productResponseMapper.toResponse(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = findProductByIdUseCase.execute(id);
        return ResponseEntity.ok(productResponseMapper.toResponse(product));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = listProductsUseCase.execute()
                .stream()
                .map(productResponseMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(products);
    }

    // REQUIERE AUTORIZACIÓN
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String authUserId) {

        deactivateProductUseCase.execute(id, authUserId);
        return ResponseEntity.noContent().build();
    }
}