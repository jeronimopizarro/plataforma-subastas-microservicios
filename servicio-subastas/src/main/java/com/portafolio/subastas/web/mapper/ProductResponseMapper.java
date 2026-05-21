package com.portafolio.subastas.web.mapper;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.web.dto.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductResponseMapper {

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

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