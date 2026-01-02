package com.joele.inventory.infrastructure.persistence.product;

import com.joele.inventory.domain.inventory.Product;

public class ProductMapper {
    public static Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getCurrentStock(),
                entity.getMinStock()
        );
    }

    public static ProductEntity toEntity(Product domain) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCurrentStock(domain.getCurrentStock());
        entity.setMinStock(domain.getMinStock());
        return entity;
    }
}
