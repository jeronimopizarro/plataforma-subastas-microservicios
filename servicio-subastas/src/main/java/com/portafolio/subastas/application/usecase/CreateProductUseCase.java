package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.application.dto.CreateProductCommand;
import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product execute(CreateProductCommand command, String authUserId) {
        Long sellerId = Long.parseLong(authUserId);
        Product product = createProduct(command, sellerId);

        return productRepository.save(product);
    }

    private Product createProduct(CreateProductCommand command, Long sellerId) {
        return Product.createNew(
                command.title(),
                command.description(),
                command.condition(),
                command.imageUrl(),
                sellerId
        );
    }
}