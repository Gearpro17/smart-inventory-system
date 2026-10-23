package com.joele.inventory.application.port;

import java.util.Optional;
import java.util.List;

import com.joele.inventory.domain.inventory.Product;

public interface ProductRepository {
    Optional<Product> findById(String productId);
    List<Product> findAll();
    List<Product> findLowStock();
    Product save(Product product);
}
