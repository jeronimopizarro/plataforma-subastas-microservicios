package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class FindProductByIdUseCase {
    private final ProductRepository productRepository;

    public FindProductByIdUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product execute(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}