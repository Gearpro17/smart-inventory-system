package com.joele.inventory.application.usecase.supplier;

public class SupplierNotFoundException extends RuntimeException {
    public SupplierNotFoundException(String supplierId) { super("Supplier not found: " + supplierId); }
}
