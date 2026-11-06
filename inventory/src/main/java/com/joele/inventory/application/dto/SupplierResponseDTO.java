package com.joele.inventory.application.dto;

import com.joele.inventory.domain.inventory.Supplier;

public record SupplierResponseDTO(String id, String name, String contactName, String email, String phone, String address, Integer leadTimeDays, String taxNumber) {
    public static SupplierResponseDTO from(Supplier supplier) {
        return new SupplierResponseDTO(supplier.getId(), supplier.getName(), supplier.getContactName(), supplier.getEmail(), supplier.getPhone(), supplier.getAddress(), supplier.getLeadTimeDays(), supplier.getTaxNumber());
    }
}
