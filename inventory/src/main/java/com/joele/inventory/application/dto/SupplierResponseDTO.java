package com.joele.inventory.application.dto;

import com.joele.inventory.domain.inventory.Supplier;

public record SupplierResponseDTO(String id, String name) {
    public static SupplierResponseDTO from(Supplier supplier) {
        return new SupplierResponseDTO(supplier.getId(), supplier.getName());
    }
}
