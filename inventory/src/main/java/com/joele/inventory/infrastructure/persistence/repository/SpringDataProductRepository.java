package com.joele.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joele.inventory.infrastructure.persistence.product.ProductEntity;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, String> {
    
}
