package com.joele.inventory.application.usecase.supplier;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.application.usecase.inventory.ProductNotFoundException;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.persistence.restock.SupplierOrderEntity;
import com.joele.inventory.infrastructure.persistence.restock.SupplierOrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierOrderService {
    private final SupplierOrderRepository orders;
    private final ProductRepository products;
    private final SupplierRepository suppliers;

    public SupplierOrderService(SupplierOrderRepository orders, ProductRepository products, SupplierRepository suppliers) {
        this.orders = orders;
        this.products = products;
        this.suppliers = suppliers;
    }

    @Transactional
    public SupplierOrderEntity create(String productId, long quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Order quantity must be greater than zero");
        Product product = products.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        if (product.getSupplierId() == null) {
            throw new IllegalArgumentException("Assign a supplier to this product before ordering stock");
        }
        var supplier = suppliers.findById(product.getSupplierId())
                .orElseThrow(() -> new SupplierNotFoundException(product.getSupplierId()));
        var order = new SupplierOrderEntity();
        order.setProductId(product.getId());
        order.setProductName(product.getName());
        order.setSupplierId(supplier.getId());
        order.setSupplierName(supplier.getName());
        order.setQuantity(quantity);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        return orders.save(order);
    }

    @Transactional(readOnly = true)
    public List<SupplierOrderEntity> list() {
        return orders.findAllByOrderByOrderDateDesc();
    }

    @Transactional
    public SupplierOrderEntity cancel(Long id) {
        var order = findPending(id);
        order.setStatus("CANCELLED");
        return orders.save(order);
    }

    @Transactional
    public void completeFromMovement(Long id, String productId, long quantity, Long movementId) {
        var order = findPending(id);
        if (!order.getProductId().equals(productId) || order.getQuantity() != quantity) {
            throw new IllegalArgumentException("Inbound movement must match the supplier order product and quantity");
        }
        order.setStatus("COMPLETED");
        order.setMovementId(movementId);
        orders.save(order);
    }

    private SupplierOrderEntity findPending(Long id) {
        var order = orders.findByIdForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier order not found"));
        if (!"PENDING".equals(order.getStatus())) {
            throw new IllegalArgumentException("Only pending supplier orders can be changed");
        }
        return order;
    }
}
