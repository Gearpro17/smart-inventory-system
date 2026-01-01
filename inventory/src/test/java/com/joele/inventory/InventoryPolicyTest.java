package com.joele.inventory;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.joele.inventory.common.DomainException;
import com.joele.inventory.common.enums.MovementType;
import com.joele.inventory.domain.inventory.InventoryPolicy;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.StockMovement;

public class InventoryPolicyTest {

    @Test
    public void testValidateMovement_NullProduct_ThrowsException() {
        StockMovement movement = new StockMovement(
            "mov1",
            "prod1",
            MovementType.IN,
            10);

        assertThrows(DomainException.class, () -> {
            InventoryPolicy.validateMovement(null, movement);
        });
    }

    @Test
    public void testValidateMovement_NullMovement_ThrowsException() {
        Product product = new Product(
            "prod1",
            "Product 1",
            100,
            10);

        assertThrows(DomainException.class, () -> {
            InventoryPolicy.validateMovement(product, null);
        });
    }

    @Test
    public void testValidateMovement_NullType_ThrowsException() {
        Product product = new Product(
            "prod1",
            "Product 1",
            100,
            10);

        assertThrows(DomainException.class, () -> {
            StockMovement movement = new StockMovement(
            "mov1",
            "prod1",
            null,
            10);
            
            InventoryPolicy.validateMovement(product, movement);
        });
    }

    @Test
    public void testValidateMovement_EmptyQuantity_ThrowsException() {
        Product product = new Product(
            "prod1",
            "Product 1",
            100,
            10);

        assertThrows(DomainException.class, () -> {
            StockMovement movement = new StockMovement(
            "mov1",
            "prod1",
            MovementType.IN,
            0L);

            InventoryPolicy.validateMovement(product, movement);
        });
    }

    @Test
    public void testValidateMovement_StockRemovalExceedsCurrentStock_ThrowsException() {
        Product product = new Product(
            "prod1",
            "Product 1",
            5,
            10);

        StockMovement movement = new StockMovement(
            "mov1",
            "prod1",
            MovementType.OUT,
            10);

        assertThrows(DomainException.class, () -> {
            InventoryPolicy.validateMovement(product, movement);
        });
    }

    @Test
    public void testValidateMovement_ValidMovement_DoesNotThrowException() {
        Product product = new Product(
            "prod1",
            "Product 1",
            100,
            10);

        StockMovement movement = new StockMovement(
            "mov1",
            "prod1",
            MovementType.IN,
            20);

        InventoryPolicy.validateMovement(product, movement);
    }

    @Test
    public void testValidateMovement_ValidStockRemoval_DoesNotThrowException() {
        Product product = new Product(
            "prod1",
            "Product 1",
            50,
            10);

        StockMovement movement = new StockMovement(
            "mov1",
            "prod1",
            MovementType.OUT,
            20);

        InventoryPolicy.validateMovement(product, movement);
    }
}
