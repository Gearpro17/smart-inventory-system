package com.joele.inventory.application.usecase.inventory;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String productId) { super("Product not found: " + productId); }
}
