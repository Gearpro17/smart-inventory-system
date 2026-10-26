package com.joele.inventory.infrastructure.persistence.supplier;

import com.joele.inventory.domain.inventory.Supplier;

public final class SupplierMapper {
    private SupplierMapper() {}

    public static Supplier toDomain(SupplierEntity entity) {
        return new Supplier(entity.getId(), entity.getName());
    }

    public static SupplierEntity toEntity(Supplier supplier) {
        SupplierEntity entity = new SupplierEntity();
        entity.setId(supplier.getId());
        entity.setName(supplier.getName());
        return entity;
    }
}
