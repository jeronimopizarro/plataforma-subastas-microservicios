package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(CreateProductCommand command) {
        Product newProduct = buildProductFrom(command);

        return productRepository.save(newProduct);
    }

    private Product buildProductFrom(CreateProductCommand command) {
        return Product.createNew(
                command.title(),
                command.description(),
                command.condition(),
                command.imageUrl(),
                command.sellerId()
        );
    }
}
