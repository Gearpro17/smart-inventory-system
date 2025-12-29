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
}
