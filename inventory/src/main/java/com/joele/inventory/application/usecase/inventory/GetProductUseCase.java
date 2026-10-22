package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import org.springframework.stereotype.Service;

@Service
public class GetProductUseCase {
    private final ProductRepository products;

    public GetProductUseCase(ProductRepository products) { this.products = products; }

    public Product execute(String productId) {
        if (productId == null || productId.isBlank()) throw new IllegalArgumentException("Product ID cannot be empty");
        return products.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }
}
