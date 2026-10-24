package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;
import java.util.Optional;

/**
 * Recommends replenishment only below the product's minimum stock. The target is
 * the configured minimum, so quantity is exactly minimum stock minus current stock.
 */
public final class ReorderPolicy {

    public Optional<ReorderSuggestion> suggestFor(Product product) {
        if (product == null) throw DomainException.of("Product cannot be null");
        if (!product.isLowStock()) return Optional.empty();

        long targetStock = product.getMinStock();
        long suggestedQuantity = targetStock - product.getCurrentStock();
        if (suggestedQuantity <= 0) return Optional.empty();

        return Optional.of(new ReorderSuggestion(
                product.getId(),
                product.getName(),
                product.getCurrentStock(),
                product.getMinStock(),
                targetStock,
                suggestedQuantity));
    }
}
