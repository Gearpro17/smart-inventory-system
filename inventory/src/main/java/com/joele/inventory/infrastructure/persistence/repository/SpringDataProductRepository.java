package com.joele.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.joele.inventory.infrastructure.persistence.product.ProductEntity;
import java.util.List;

public interface SpringDataProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findAllByOrderByNameAscIdAsc();

    @Query("select product from ProductEntity product where product.currentStock < product.minStock order by product.name asc, product.id asc")
    List<ProductEntity> findLowStock();
}
