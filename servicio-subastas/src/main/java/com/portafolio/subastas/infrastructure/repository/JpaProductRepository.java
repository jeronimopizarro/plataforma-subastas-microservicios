package com.portafolio.subastas.infrastructure.repository;

import com.portafolio.subastas.infrastructure.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
}
