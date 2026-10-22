package com.joele.inventory.application.dto;

import com.joele.inventory.domain.inventory.Product;

public record ProductResponseDTO(String id, String name, long currentStock, long minStock, boolean lowStock) {
    public static ProductResponseDTO from(Product product) {
        return new ProductResponseDTO(product.getId(), product.getName(), product.getCurrentStock(), product.getMinStock(), product.isLowStock());
    }
}
