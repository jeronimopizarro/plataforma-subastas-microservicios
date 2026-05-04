package com.portafolio.subastas.infrastructure.adapter;

import com.portafolio.subastas.domain.entity.Product;
import com.portafolio.subastas.domain.repository.ProductRepository;
import com.portafolio.subastas.infrastructure.entity.ProductEntity;
import com.portafolio.subastas.infrastructure.mapper.ProductMapper;
import com.portafolio.subastas.infrastructure.repository.JpaProductRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaRepository;
    private final ProductMapper mapper;

    public ProductRepositoryAdapter(JpaProductRepository jpaRepository, ProductMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entityToSave = mapper.toEntity(product);

        ProductEntity savedEntity = jpaRepository.save(entityToSave);

        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
}
