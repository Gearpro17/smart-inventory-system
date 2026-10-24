package com.joele.inventory.application.dto;

import com.joele.inventory.domain.inventory.ReorderSuggestion;

public record ReorderSuggestionResponseDTO(
        String productId,
        String productName,
        long currentStock,
        long minStock,
        long targetStock,
        long suggestedQuantity) {

    public static ReorderSuggestionResponseDTO from(ReorderSuggestion suggestion) {
        return new ReorderSuggestionResponseDTO(
                suggestion.productId(),
                suggestion.productName(),
                suggestion.currentStock(),
                suggestion.minStock(),
                suggestion.targetStock(),
                suggestion.suggestedQuantity());
    }
}
