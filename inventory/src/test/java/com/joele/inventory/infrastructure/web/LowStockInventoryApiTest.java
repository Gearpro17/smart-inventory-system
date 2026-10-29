package com.joele.inventory.infrastructure.web;

import com.joele.inventory.application.dto.ProductResponseDTO;
import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.web.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("dev")
class LowStockInventoryApiTest {
    @Autowired
    private ProductRepository products;

    @Autowired
    private ProductController controller;

    @Test
    void inventoryEndpointsReportStockAndReturnProductsAtOrBelowMinimum() {
        products.save(new Product("above-minimum", "Above minimum", 8, 5));
        products.save(new Product("at-minimum", "At minimum", 5, 5));
        products.save(new Product("below-minimum", "Below minimum", 2, 5));

        List<ProductResponseDTO> inventory = controller.list();
        List<ProductResponseDTO> lowStock = controller.listLowStock();
        var suggestions = controller.reorderSuggestions();

        assertEquals(3, inventory.size());
        assertTrue(inventory.stream().filter(p -> p.id().equals("below-minimum")).findFirst().orElseThrow().lowStock());
        assertTrue(inventory.stream().filter(p -> p.id().equals("at-minimum")).findFirst().orElseThrow().lowStock());
        assertFalse(inventory.stream().filter(p -> p.id().equals("above-minimum")).findFirst().orElseThrow().lowStock());
        assertEquals(List.of("at-minimum", "below-minimum"), lowStock.stream().map(ProductResponseDTO::id).toList());
        ProductResponseDTO atMinimum = lowStock.stream()
                .filter(product -> product.id().equals("at-minimum"))
                .findFirst().orElseThrow();
        ProductResponseDTO belowMinimum = lowStock.stream()
                .filter(product -> product.id().equals("below-minimum"))
                .findFirst().orElseThrow();
        assertEquals(5, atMinimum.currentStock());
        assertTrue(atMinimum.lowStock());
        assertEquals("LOW", atMinimum.stockStatus().name());
        assertEquals(2, belowMinimum.currentStock());
        assertEquals(5, belowMinimum.minStock());
        assertTrue(belowMinimum.lowStock());
        assertEquals("LOW", belowMinimum.stockStatus().name());
        assertEquals(2, suggestions.size());
        var atMinimumSuggestion = suggestions.stream()
                .filter(suggestion -> suggestion.productId().equals("at-minimum"))
                .findFirst().orElseThrow();
        var belowMinimumSuggestion = suggestions.stream()
                .filter(suggestion -> suggestion.productId().equals("below-minimum"))
                .findFirst().orElseThrow();
        assertEquals(8, atMinimumSuggestion.targetStock());
        assertEquals(3, atMinimumSuggestion.suggestedQuantity());
        assertEquals(8, belowMinimumSuggestion.targetStock());
        assertEquals(6, belowMinimumSuggestion.suggestedQuantity());
    }
}
