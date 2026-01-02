package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.common.enums.MovementType;

public record RegisterStockMovementCommand(
    String productId,
    MovementType type,
    long quantity) {} 