package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.domain.inventory.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateSupplierUseCase {
    private final SupplierRepository suppliers;

    public UpdateSupplierUseCase(SupplierRepository suppliers) { this.suppliers = suppliers; }

    @Transactional
    public Supplier execute(String id, String name) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Supplier ID cannot be empty");
        String normalizedId = id.trim();
        if (suppliers.findById(normalizedId).isEmpty()) throw new SupplierNotFoundException(normalizedId);
        return suppliers.save(new Supplier(normalizedId, name));
    }
}
