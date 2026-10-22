package com.joele.inventory.application.port;

import com.joele.inventory.domain.inventory.StockMovement;
import java.util.List;

public interface StockMovementRepository {
    StockMovement save(StockMovement movement);
    List<StockMovement> findByProductId(String productId);
}
