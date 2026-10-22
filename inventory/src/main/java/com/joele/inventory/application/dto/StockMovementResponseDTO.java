package com.joele.inventory.application.dto;

import com.joele.inventory.common.enums.MovementType;
import com.joele.inventory.domain.inventory.StockMovement;
import java.time.LocalDateTime;

public record StockMovementResponseDTO(Long id, String productId, MovementType type, long quantity, LocalDateTime timestamp) {
    public static StockMovementResponseDTO from(StockMovement movement) {
        return new StockMovementResponseDTO(movement.getId(), movement.getProductId(), movement.getType(), movement.getQuantity(), movement.getTimestamp());
    }
}
