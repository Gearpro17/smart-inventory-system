package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.domain.inventory.Supplier;
import org.springframework.stereotype.Service;

@Service
public class GetSupplierUseCase {
    private final SupplierRepository suppliers;

    public GetSupplierUseCase(SupplierRepository suppliers) { this.suppliers = suppliers; }

    public Supplier execute(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Supplier ID cannot be empty");
        return suppliers.findById(id).orElseThrow(() -> new SupplierNotFoundException(id));
    }
}
