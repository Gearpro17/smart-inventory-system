package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;

/** A recommendation to replenish a product to its configured minimum stock level. */
public record ReorderSuggestion(
        String productId,
        String productName,
        long currentStock,
        long minStock,
        long targetStock,
        long suggestedQuantity) {

    public ReorderSuggestion {
        if (productId == null || productId.isBlank()) throw DomainException.of("Product ID cannot be empty");
        if (productName == null || productName.isBlank()) throw DomainException.of("Product name cannot be empty");
        if (currentStock < 0) throw DomainException.of("Current stock cannot be less than zero");
        if (minStock < 0) throw DomainException.of("Minimum stock cannot be less than zero");
        if (targetStock < 0) throw DomainException.of("Target stock cannot be less than zero");
        if (targetStock <= currentStock) throw DomainException.of("Target stock must be greater than current stock");
        if (suggestedQuantity <= 0 || suggestedQuantity != targetStock - currentStock) {
            throw DomainException.of("Suggested quantity must equal the positive quantity needed to reach target stock");
        }
    }
}
