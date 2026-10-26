package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;

public class Supplier {
    private final String id;
    private final String name;

    public Supplier(String id, String name) {
        if (id == null || id.isBlank()) throw DomainException.of("Supplier ID cannot be empty");
        if (name == null || name.isBlank()) throw DomainException.of("Supplier name cannot be empty");
        if (id.trim().length() > 100) throw DomainException.of("Supplier ID cannot exceed 100 characters");
        if (name.trim().length() > 150) throw DomainException.of("Supplier name cannot exceed 150 characters");
        this.id = id.trim();
        this.name = name.trim();
    }

    public String getId() { return id; }
    public String getName() { return name; }
}
