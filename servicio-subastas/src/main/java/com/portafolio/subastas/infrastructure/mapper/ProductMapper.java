package com.portafolio.subastas.infrastructure.mapper;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.infrastructure.entity.ProductEntity;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductEntity toEntity(Product domain) {
        if (domain == null) {
            return null;
        }

        return ProductEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .condition(domain.getCondition())
                .imageUrl(domain.getImageUrl())
                .sellerId(domain.getSellerId())
                .active(domain.isActive())
                .build();
    }

    public Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }

        return Product.restore(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCondition(),
                entity.getImageUrl(),
                entity.getSellerId(),
                entity.isActive()
        );
    }
}
