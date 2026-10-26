package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.domain.inventory.Supplier;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListSuppliersUseCase {
    private final SupplierRepository suppliers;

    public ListSuppliersUseCase(SupplierRepository suppliers) { this.suppliers = suppliers; }

    public List<Supplier> execute() { return suppliers.findAll(); }
}
