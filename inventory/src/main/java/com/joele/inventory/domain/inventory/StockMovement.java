package com.joele.inventory.domain.inventory;

import java.time.LocalDateTime;

import com.joele.inventory.common.DomainException;
import com.joele.inventory.common.enums.MovementType;

public class StockMovement {

    private final String id;
    private final String productId;
    private final MovementType type;
    private final long quantity;
    private final LocalDateTime timestamp;

    public StockMovement(String id, String productId, MovementType type, long quantity) {
        if (id == null || id.isBlank()) throw new DomainException("Movement ID cannot be empty");
        if (productId == null || productId.isBlank()) throw new DomainException("Product ID cannot be empty");
        if (quantity <= 0) throw new DomainException("Quantity must be greater than zero");
        if (type == null) throw new DomainException("Movement type cannot be null");

        this.id = id;
        this.productId = productId;
        this.type = type;
        this.quantity = quantity;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getProductId() { return productId; }
    public MovementType getType() { return type; }
    public long getQuantity() { return quantity; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Derived behavior
    public long getSignedQuantity() {
        return type == MovementType.IN ? quantity : -quantity;
    }
}
