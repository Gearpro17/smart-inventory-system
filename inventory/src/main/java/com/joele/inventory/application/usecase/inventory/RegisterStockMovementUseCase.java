package com.joele.inventory.application.usecase.inventory;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.StockMovementRepository;
import com.joele.inventory.domain.inventory.InventoryPolicy;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.StockMovement;

@Service
public class RegisterStockMovementUseCase {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final com.joele.inventory.application.usecase.supplier.SupplierOrderService supplierOrders;

    @org.springframework.beans.factory.annotation.Autowired
    public RegisterStockMovementUseCase(ProductRepository productRepository,
                                        StockMovementRepository stockMovementRepository,
                                        com.joele.inventory.application.usecase.supplier.SupplierOrderService supplierOrders) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.supplierOrders = supplierOrders;
    }

    public RegisterStockMovementUseCase(ProductRepository productRepository, StockMovementRepository stockMovementRepository) {
        this(productRepository, stockMovementRepository, null);
    }

    @Transactional
    public StockMovement execute(RegisterStockMovementCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        // Validate product existence and load product
        Product product = productRepository.findById(command.productId())
            .orElseThrow(() -> new ProductNotFoundException(command.productId()));

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
        StockMovement savedMovement = stockMovementRepository.save(movement);
        productRepository.save(updatedProduct);
        if (command.supplierOrderId() != null) {
            if (supplierOrders == null) throw new IllegalStateException("Supplier order processing is unavailable");
            if (command.type() != com.joele.inventory.common.enums.MovementType.IN) throw new IllegalArgumentException("Supplier orders require an inbound movement");
            supplierOrders.completeFromMovement(command.supplierOrderId(), command.productId(), command.quantity(), savedMovement.getId());
        }
        return savedMovement;
    }
}
