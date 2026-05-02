package com.portafolio.subastas.domain.repository;

import com.portafolio.subastas.domain.entity.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
}
