package com.joele.inventory.infrastructure.persistence.product;

import com.joele.inventory.domain.inventory.Supplier;
import com.joele.inventory.infrastructure.persistence.supplier.SupplierMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SupplierPersistenceMappingTest {
    @Test
    void productSupplierAssociationMapsToAndFromPersistenceEntity() {
        var supplier = SupplierMapper.toEntity(new Supplier("supplier-1", "Acme Supply"));

        var entity = new ProductEntity();
        entity.setId("product-1");
        entity.setName("Widget");
        entity.setCurrentStock(4);
        entity.setMinStock(2);
        entity.setSupplier(supplier);

        var product = ProductMapper.toDomain(entity);

        assertEquals("supplier-1", product.getSupplierId());
        var roundTripped = ProductMapper.toDomain(ProductMapper.toEntity(product, supplier));
        assertEquals("supplier-1", roundTripped.getSupplierId());
        assertNull(ProductMapper.toDomain(ProductMapper.toEntity(
                new com.joele.inventory.domain.inventory.Product("unassigned", "Widget", 0, 0), null)).getSupplierId());
    }
}
