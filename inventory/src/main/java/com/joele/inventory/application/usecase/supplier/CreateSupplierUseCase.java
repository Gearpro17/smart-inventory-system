package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.domain.inventory.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateSupplierUseCase {
    private final SupplierRepository suppliers;

    public CreateSupplierUseCase(SupplierRepository suppliers) { this.suppliers = suppliers; }

    @Transactional
    public Supplier execute(String id, String name) {
        return execute(id, name, null, null, null, null, null, null);
    }

    public Supplier execute(String id, String name, String contactName, String email, String phone, String address, Integer leadTimeDays, String taxNumber) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Supplier ID is required");
        String normalizedId = id.trim();
        if (suppliers.findById(normalizedId).isPresent()) throw new IllegalArgumentException("Supplier with ID " + normalizedId + " already exists");
        return suppliers.save(new Supplier(normalizedId, name, contactName, email, phone, address, leadTimeDays, taxNumber));
    }
}
