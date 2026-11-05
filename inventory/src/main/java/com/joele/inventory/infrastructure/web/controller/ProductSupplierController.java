package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.AssignSupplierRequestDTO;
import com.joele.inventory.application.dto.ProductResponseDTO;
import com.joele.inventory.application.usecase.supplier.AssignSupplierToProductUseCase;
import com.joele.inventory.application.usecase.supplier.RemoveSupplierFromProductUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/{productId}/supplier")
public class ProductSupplierController {
    private final AssignSupplierToProductUseCase assignSupplier;
    private final RemoveSupplierFromProductUseCase removeSupplier;

    public ProductSupplierController(AssignSupplierToProductUseCase assignSupplier,
                                     RemoveSupplierFromProductUseCase removeSupplier) {
        this.assignSupplier = assignSupplier;
        this.removeSupplier = removeSupplier;
    }

    @PutMapping
    public ProductResponseDTO assign(@PathVariable String productId, @RequestBody AssignSupplierRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Supplier ID is required");
        return ProductResponseDTO.from(assignSupplier.execute(productId, request.supplierId()));
    }

    @DeleteMapping
    public ProductResponseDTO remove(@PathVariable String productId) {
        return ProductResponseDTO.from(removeSupplier.execute(productId));
    }
}
