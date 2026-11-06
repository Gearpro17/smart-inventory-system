package com.joele.inventory.application.dto;

import com.joele.inventory.infrastructure.persistence.restock.SupplierOrderEntity;
import java.time.LocalDateTime;

public record SupplierOrderDTO(Long id, String productId, String productName, String supplierId, String supplierName, long quantity, LocalDateTime orderDate, String status, Long movementId) {
    public static SupplierOrderDTO from(SupplierOrderEntity o) { return new SupplierOrderDTO(o.getId(),o.getProductId(),o.getProductName(),o.getSupplierId(),o.getSupplierName(),o.getQuantity(),o.getOrderDate(),o.getStatus(),o.getMovementId()); }
}
