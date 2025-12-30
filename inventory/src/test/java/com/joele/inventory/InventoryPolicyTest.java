package com.joele.inventory;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.joele.inventory.common.DomainException;
import com.joele.inventory.common.enums.MovementType;
import com.joele.inventory.domain.inventory.InventoryPolicy;
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
}
