package com.joele.inventory.infrastructure.persistence.product;

import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.Supplier;
import com.joele.inventory.infrastructure.persistence.supplier.SupplierMapper;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SupplierJpaPersistenceTest {
    @Test
    void productSupplierAssociationSurvivesDatabaseReload() {
        try (var factory = Persistence.createEntityManagerFactory("supplier-persistence-test");
             var entityManager = factory.createEntityManager()) {
            entityManager.getTransaction().begin();
            var supplier = SupplierMapper.toEntity(new Supplier("supplier-1", "Acme Supply"));
            entityManager.persist(supplier);
            var productEntity = ProductMapper.toEntity(new Product("product-1", "Widget", 4, 2, "supplier-1"), supplier);
            entityManager.persist(productEntity);
            entityManager.getTransaction().commit();

            entityManager.clear();
            Product reloaded = ProductMapper.toDomain(entityManager.find(ProductEntity.class, "product-1"));

            assertEquals("supplier-1", reloaded.getSupplierId());
        }
    }
}
