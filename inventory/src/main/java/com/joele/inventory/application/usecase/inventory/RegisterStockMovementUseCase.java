package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.StockMovementRepository;
import com.joele.inventory.common.DomainException;
import com.joele.inventory.domain.inventory.InventoryPolicy;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.StockMovement;

public class RegisterStockMovementUseCase {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public RegisterStockMovementUseCase(ProductRepository productRepository,
                                        StockMovementRepository stockMovementRepository) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public void execute(RegisterStockMovementCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        // Validate product existence and load product
        Product product = productRepository.findById(command.productId());
        if (product == null) {
        throw DomainException.of("Product not found");
        }

        // Create and save stock movement
        StockMovement movement = new StockMovement(
            null,
            command.productId(),
            command.type(),
            command.quantity()
        );

        // Validate movement
        InventoryPolicy.validateMovement(product, movement);

        // Apply movement to product stock
        Product updatedProduct = product.applyMovement(movement);

        // Persist changes
        productRepository.save(updatedProduct);
        stockMovementRepository.save(movement);
    }
}
