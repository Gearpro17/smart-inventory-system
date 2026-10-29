package com.joele.inventory.application.dto;

import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.StockStatus;

public record ProductResponseDTO(String id, String name, long currentStock, long minStock, boolean lowStock, String supplierId, StockStatus stockStatus) {
    public static ProductResponseDTO from(Product product) {
        return new ProductResponseDTO(product.getId(), product.getName(), product.getCurrentStock(), product.getMinStock(), product.isLowStock(), product.getSupplierId(), product.getStockStatus());
    }
}
