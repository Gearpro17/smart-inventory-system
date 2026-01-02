package com.joele.inventory.application.port;

import com.joele.inventory.domain.inventory.Product;

public interface ProductRepository {
    Product findById(String productId);
    void save(Product product);
}
