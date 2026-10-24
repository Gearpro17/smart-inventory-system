package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReorderPolicyTest {
    private final ReorderPolicy policy = new ReorderPolicy();

    @Test
    void suggestsExactlyEnoughToReachMinimumWhenBelowMinimum() {
        var suggestion = policy.suggestFor(new Product("p-1", "Widget", 4, 10)).orElseThrow();

        assertEquals("p-1", suggestion.productId());
        assertEquals("Widget", suggestion.productName());
        assertEquals(4, suggestion.currentStock());
        assertEquals(10, suggestion.minStock());
        assertEquals(10, suggestion.targetStock());
        assertEquals(6, suggestion.suggestedQuantity());
    }

    @Test
    void doesNotSuggestAtOrAboveMinimum() {
        assertTrue(policy.suggestFor(new Product("at", "At minimum", 10, 10)).isEmpty());
        assertTrue(policy.suggestFor(new Product("above", "Above minimum", 11, 10)).isEmpty());
    }

    @Test
    void zeroStockSuggestsTheFullMinimumWhenMinimumIsPositive() {
        var suggestion = policy.suggestFor(new Product("empty", "Empty", 0, 5)).orElseThrow();

        assertEquals(5, suggestion.targetStock());
        assertEquals(5, suggestion.suggestedQuantity());
    }

    @Test
    void zeroMinimumAndZeroStockDoesNotCreateAnActionableSuggestion() {
        assertTrue(policy.suggestFor(new Product("zero", "Zero minimum", 0, 0)).isEmpty());
    }

    @Test
    void producesTheSameSuggestionForTheSameProduct() {
        var product = new Product("p-1", "Widget", 4, 10);

        assertEquals(policy.suggestFor(product), policy.suggestFor(new Product("p-1", "Widget", 4, 10)));
    }

    @Test
    void rejectsMissingProductInput() {
        assertThrows(DomainException.class, () -> policy.suggestFor(null));
    }

    @Test
    void rejectsInvalidProductStockConfiguration() {
        assertThrows(DomainException.class, () -> new Product("p-1", "Widget", -1, 10));
        assertThrows(DomainException.class, () -> new Product("p-1", "Widget", 1, -1));
    }

    @Test
    void suggestionRequiresPositiveQuantityThatReachesTarget() {
        assertThrows(DomainException.class, () -> new ReorderSuggestion("p-1", "Widget", 5, 5, 5, 0));
        assertThrows(DomainException.class, () -> new ReorderSuggestion("p-1", "Widget", 1, 5, 5, 3));
    }
}
