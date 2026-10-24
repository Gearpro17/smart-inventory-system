package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GenerateReorderSuggestionsUseCaseTest {
    @Test
    void calculatesSuggestionsFromRepositoryProductsAndSkipsNonQualifyingItems() {
        var products = new StubProductRepository(List.of(
                new Product("low", "Low stock", 2, 5),
                new Product("at", "At minimum", 5, 5),
                new Product("above", "Above minimum", 7, 5)));

        var suggestions = new GenerateReorderSuggestionsUseCase(products).execute();

        assertEquals(1, suggestions.size());
        assertEquals("low", suggestions.getFirst().productId());
        assertEquals(3, suggestions.getFirst().suggestedQuantity());
        assertEquals(1, products.findLowStockCalls);
    }

    @Test
    void returnsAnEmptyListWhenThereAreNoEligibleProducts() {
        ProductRepository products = new StubProductRepository(List.of());

        assertEquals(List.of(), new GenerateReorderSuggestionsUseCase(products).execute());
    }

    private static class StubProductRepository implements ProductRepository {
        private final List<Product> lowStockProducts;
        private int findLowStockCalls;

        private StubProductRepository(List<Product> lowStockProducts) { this.lowStockProducts = lowStockProducts; }

        @Override public Optional<Product> findById(String productId) { return Optional.empty(); }
        @Override public List<Product> findAll() { return lowStockProducts; }
        @Override public List<Product> findLowStock() { findLowStockCalls++; return lowStockProducts; }
        @Override public Product save(Product product) { return product; }
    }
}
