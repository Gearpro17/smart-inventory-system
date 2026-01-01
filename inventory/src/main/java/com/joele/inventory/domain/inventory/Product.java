package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;

public class Product {
    private final String id;
    private final String name;
    private final long currentStock;
    private final long minStock;

    public Product(String id, String name, long currentStock, long minStock){
        if (id == null || id.isEmpty())     throw DomainException.of("Id cannot be empty");
        if (name == null || name.isEmpty()) throw DomainException.of("Product name cannot be empty");
        if (currentStock < 0)               throw DomainException.of("Current stock cannot be less than zero");
        if (minStock < 0)                   throw DomainException.of("Minimum stock cannot be less than zero");

        this.id = id;
        this.name = name;
        this.currentStock = currentStock;
        this.minStock = minStock;
    }

    // Getters
    public String getId() {return id;}
    public String getName() {return name;}
    public Long getCurrentStock() {return currentStock;}
    public Long getMinStock() {return minStock;}

    // Derived Behaviour
    public Product applyMovement(StockMovement movement){
        if (!movement.getProductId().equals(this.id)){
            throw DomainException.of("Movement product ID does not match");
        }

        long updatedStock = this.currentStock + movement.getSignedQuantity();

        return new Product(
            this.id,
            this.name,
            updatedStock,
            this.minStock);
    }

    public boolean isLowStock(){
        return this.currentStock < this.minStock;
    }

    public boolean isOutOfStock(){
        return this.currentStock == 0;
    }

    public boolean canRemoveStock(long quantity){
        return this.currentStock - quantity >= 0;
    }
}
