package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReorderPolicyTest {
    private final ReorderPolicy policy = new ReorderPolicy();

    @Test
    void suggestsExactlyEnoughToReachTargetWhenBelowMinimum() {
        var suggestion = policy.suggestFor(new Product("p-1", "Widget", 4, 10)).orElseThrow();

        assertEquals("p-1", suggestion.productId());
        assertEquals("Widget", suggestion.productName());
        assertEquals(4, suggestion.currentStock());
        assertEquals(10, suggestion.minStock());
        assertEquals(15, suggestion.targetStock());
        assertEquals(11, suggestion.suggestedQuantity());
    }

    @Test
    void suggestsAtMinimumAndDoesNotSuggestForHealthyStock() {
        var atMinimum = policy.suggestFor(new Product("at", "At minimum", 10, 10)).orElseThrow();
        assertEquals(15, atMinimum.targetStock());
        assertEquals(5, atMinimum.suggestedQuantity());
        assertTrue(policy.suggestFor(new Product("target", "At reorder target", 15, 10)).isEmpty());
        assertTrue(policy.suggestFor(new Product("above", "Above reorder target", 16, 10)).isEmpty());
    }

    @Test
    void zeroStockSuggestsTheFullReorderTargetWhenMinimumIsPositive() {
        var suggestion = policy.suggestFor(new Product("empty", "Empty", 0, 5)).orElseThrow();

        assertEquals(8, suggestion.targetStock());
        assertEquals(8, suggestion.suggestedQuantity());
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
