package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SupplierTest {
    @Test
    void trimsSupplierIdentityAndName() {
        var supplier = new Supplier(" supplier-1 ", " Acme Supply ");

        assertEquals("supplier-1", supplier.getId());
        assertEquals("Acme Supply", supplier.getName());
    }

    @Test
    void rejectsMissingIdentityOrName() {
        assertAll(
                () -> assertThrows(DomainException.class, () -> new Supplier(" ", "Acme")),
                () -> assertThrows(DomainException.class, () -> new Supplier("s-1", " "))
        );
    }

    @Test
    void productAssociationIsPreservedWhenStockChanges() {
        var product = new Product("p-1", "Widget", 3, 1, "supplier-1");
        var moved = product.applyMovement(new StockMovement(null, "p-1", com.joele.inventory.common.enums.MovementType.IN, 2));

        assertEquals("supplier-1", moved.getSupplierId());
        assertEquals(5, moved.getCurrentStock());
    }
}
