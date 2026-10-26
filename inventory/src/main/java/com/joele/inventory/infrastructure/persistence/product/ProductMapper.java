package com.joele.inventory.infrastructure.persistence.product;

import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.persistence.supplier.SupplierEntity;

public class ProductMapper {
    public static Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                entity.getCurrentStock(),
                entity.getMinStock(),
                entity.getSupplier() == null ? null : entity.getSupplier().getId()
        );
    }

    public static ProductEntity toEntity(Product domain, SupplierEntity supplier) {
        ProductEntity entity = new ProductEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setCurrentStock(domain.getCurrentStock());
        entity.setMinStock(domain.getMinStock());
        entity.setSupplier(supplier);
        return entity;
    }
}
