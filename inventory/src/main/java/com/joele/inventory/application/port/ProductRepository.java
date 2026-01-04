package com.joele.inventory.application.port;

import java.util.Optional;

import com.joele.inventory.domain.inventory.Product;

public interface ProductRepository {
    Optional<Product> findById(String productId);
    Product save(Product product);
}
