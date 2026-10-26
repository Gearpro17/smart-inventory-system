package com.joele.inventory.application.port;

import com.joele.inventory.domain.inventory.Supplier;
import java.util.List;
import java.util.Optional;

public interface SupplierRepository {
    Optional<Supplier> findById(String supplierId);
    List<Supplier> findAll();
    Supplier save(Supplier supplier);
}
