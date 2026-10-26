package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.usecase.inventory.ProductNotFoundException;
import com.joele.inventory.domain.inventory.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveSupplierFromProductUseCase {
    private final ProductRepository products;

    public RemoveSupplierFromProductUseCase(ProductRepository products) { this.products = products; }

    @Transactional
    public Product execute(String productId) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID cannot be empty");
        Product product = products.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return products.save(product.withSupplier(null));
    }
}
