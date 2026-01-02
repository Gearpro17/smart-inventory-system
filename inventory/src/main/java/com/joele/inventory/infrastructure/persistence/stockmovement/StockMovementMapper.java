package com.joele.inventory.infrastructure.persistence.stockmovement;

import com.joele.inventory.domain.inventory.StockMovement;

public class StockMovementMapper {

    public static StockMovement toDomain(StockMovementEntity entity) {
        return new StockMovement(
                entity.getId(),
                entity.getProductId(),
                entity.getType(),
                entity.getQuantity()
            );
    }

    public static StockMovementEntity toEntity(StockMovement domain) {
        StockMovementEntity entity = new StockMovementEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProductId());
        entity.setType(domain.getType());
        entity.setQuantity(domain.getQuantity());
        entity.setTimestamp(domain.getTimestamp());
        return entity;
    }
}