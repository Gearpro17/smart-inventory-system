package com.joele.inventory.domain.inventory;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.joele.inventory.common.DomainException;
import com.joele.inventory.common.enums.MovementType;

public class ProductTest {

    @Test
    void testProduct_NullId_ThrowsException() {
        assertThrows(DomainException.class, () -> {
            new Product(
                null,
                "prod1",
                10,
                1)
            ;});
    }

    @Test
    void testProduct_EmptyName_ThrowsException() {
        assertThrows(DomainException.class, () -> {
            new Product(
                "prod1",
                "",
                10,
                1)
            ;});
    }

    @Test
    void testProduct_NegativeCurrentStock_ThrowsException() {
        assertThrows(DomainException.class, () -> {
            new Product(
                "prod1",
                "Product 1",
                -5,
                1)
            ;});
        }
    
    @Test
    void testProduct_NegativeMinStock_ThrowsException() {
        assertThrows(DomainException.class, () -> {
            new Product(
                "prod1",
                "Product 1",
                10,
                -5)
            ;});
    }

    @Test
    void testProduct_ValidParameters_CreatesProduct() {
        new Product(
            "prod1",
            "Product 1",
            10,
            5);
    }

    @Test
    void stockAtMinimumIsLowAndStockWithinFiftyPercentAboveIsApproaching() {
        assertEquals(StockStatus.LOW, new Product("at", "At minimum", 100, 100).getStockStatus());
        assertEquals(StockStatus.APPROACHING_MINIMUM, new Product("near", "Near minimum", 150, 100).getStockStatus());
        assertEquals(StockStatus.HEALTHY, new Product("healthy", "Healthy", 151, 100).getStockStatus());
    }

    @Test
    void testProduct_ApplyMovement_ReturnsNewProduct() {
        Product product = new Product(
            "prod1",
            "Product 1",
            10,
            5);

        var movement = new StockMovement(
            1l,
            "prod1",
            MovementType.IN,
            5);

        Product updatedProduct = product.applyMovement(movement);

        assert(updatedProduct.getCurrentStock() == 15);
        assert(product.getCurrentStock() == 10);
    }   
}
