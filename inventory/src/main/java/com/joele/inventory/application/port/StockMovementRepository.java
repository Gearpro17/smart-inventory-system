package com.joele.inventory.application.port;

import com.joele.inventory.domain.inventory.StockMovement;

public interface StockMovementRepository {
    StockMovement save(StockMovement movement);
}
