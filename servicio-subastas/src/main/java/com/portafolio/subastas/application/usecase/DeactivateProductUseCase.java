package com.portafolio.subastas.application.usecase;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.exception.ProductNotFoundException;
import com.portafolio.subastas.domain.exception.UnauthorizedAccessException;
import com.portafolio.subastas.domain.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateProductUseCase {

    private final ProductRepository productRepository;

    public DeactivateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void execute(Long productId, String authUserId) {
        Product product = findProductById(productId);

        validateSameOwner(authUserId, product);

        product.deactivate();
        productRepository.save(product);
    }

    private static void validateSameOwner(String authUserId, Product product) {
        if (!product.getSellerId().toString().equals(authUserId)) {
            throw new UnauthorizedAccessException("Error 403: No tienes permisos para desactivar este producto.");
        }
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }
}