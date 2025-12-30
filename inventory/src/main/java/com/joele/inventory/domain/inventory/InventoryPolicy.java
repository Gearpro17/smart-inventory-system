package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;
import com.joele.inventory.common.enums.MovementType;

public class InventoryPolicy {
    public static void validateMovement(Product product, StockMovement movement) {
        if(product == null){
            throw DomainException.of("product cannot be null");
        }
        if(movement == null){
            throw DomainException.of("movement cannot be null");
        }
        if(movement.getType() == null){
            throw DomainException.of("movement type cannot be null");
        }
        if(movement.getQuantity() <= 0){
            throw DomainException.of("movement quantity must be greater than zero");
        }
        if(movement.getType() == MovementType.OUT && movement.getQuantity() > product.getCurrentStock()){
            throw DomainException.of("movement quantity exceeds available stock");
        }
    }   
}
