package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.common.enums.MovementType;

public record RegisterStockMovementCommand(
    String productId,
    MovementType type,
    long quantity,
    Long supplierOrderId) {
    public RegisterStockMovementCommand(String productId, com.joele.inventory.common.enums.MovementType type, long quantity) { this(productId, type, quantity, null); }
}
