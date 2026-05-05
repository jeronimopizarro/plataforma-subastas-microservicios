package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class DeactivateProductUseCase {

    private final ProductRepository productRepository;

    public DeactivateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void execute(Long id) {
        Product product = findProductOrThrow(id);

        product.deactivate();
        
        productRepository.save(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
