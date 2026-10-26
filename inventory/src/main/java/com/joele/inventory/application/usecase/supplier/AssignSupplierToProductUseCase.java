package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.application.usecase.inventory.ProductNotFoundException;
import com.joele.inventory.domain.inventory.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignSupplierToProductUseCase {
    private final ProductRepository products;
    private final SupplierRepository suppliers;

    public AssignSupplierToProductUseCase(ProductRepository products, SupplierRepository suppliers) {
        this.products = products;
        this.suppliers = suppliers;
    }

    @Transactional
    public Product execute(String productId, String supplierId) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID cannot be empty");
        if (supplierId == null || supplierId.isBlank()) throw new IllegalArgumentException("Supplier ID is required");
        Product product = products.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        String normalizedSupplierId = supplierId.trim();
        if (suppliers.findById(normalizedSupplierId).isEmpty()) throw new SupplierNotFoundException(normalizedSupplierId);
        return products.save(product.withSupplier(normalizedSupplierId));
    }
}
