package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;
import java.util.Optional;

/** Recommends replenishment for low-stock products to 150% of minimum stock. */
public final class ReorderPolicy {

    public Optional<ReorderSuggestion> suggestFor(Product product) {
        if (product == null) throw DomainException.of("Product cannot be null");
        if (product.getStockStatus() != StockStatus.LOW) return Optional.empty();

        long minStock = product.getMinStock();
        if (minStock == 0) return Optional.empty();

        long buffer = minStock / 2 + (minStock % 2 == 0 ? 0 : 1);
        long targetStock = minStock > Long.MAX_VALUE - buffer ? Long.MAX_VALUE : minStock + buffer;
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
