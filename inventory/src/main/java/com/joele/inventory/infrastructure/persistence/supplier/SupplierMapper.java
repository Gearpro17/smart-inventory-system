package com.joele.inventory.infrastructure.persistence.supplier;

import com.joele.inventory.domain.inventory.Supplier;

public final class SupplierMapper {
    private SupplierMapper() {}

    public static Supplier toDomain(SupplierEntity entity) {
        return new Supplier(entity.getId(), entity.getName(), entity.getContactName(), entity.getEmail(), entity.getPhone(), entity.getAddress(), entity.getLeadTimeDays(), entity.getTaxNumber());
    }

    public static SupplierEntity toEntity(Supplier supplier) {
        SupplierEntity entity = new SupplierEntity();
        entity.setId(supplier.getId());
        entity.setName(supplier.getName());
        entity.setContactName(supplier.getContactName()); entity.setEmail(supplier.getEmail()); entity.setPhone(supplier.getPhone());
        entity.setAddress(supplier.getAddress()); entity.setLeadTimeDays(supplier.getLeadTimeDays()); entity.setTaxNumber(supplier.getTaxNumber());
        return entity;
    }
}
