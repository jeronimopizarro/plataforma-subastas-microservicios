package com.portafolio.subastas.web.controller;

import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.application.usecase.CreateProductUseCase;
import com.portafolio.subastas.application.usecase.FindProductByIdUseCase;
import com.portafolio.subastas.application.usecase.ListProductsUseCase;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.web.dto.ProductResponse;
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

    public ProductController(CreateProductUseCase createProductUseCase,
                             FindProductByIdUseCase findProductByIdUseCase,
                             ListProductsUseCase listProductsUseCase) {
        this.createProductUseCase = createProductUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.listProductsUseCase = listProductsUseCase;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductCommand command) {
        Product savedProduct = createProductUseCase.execute(command);

        ProductResponse response = mapToResponse(savedProduct);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = findProductByIdUseCase.execute(id);
        return ResponseEntity.ok(mapToResponse(product));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = listProductsUseCase.execute()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
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
