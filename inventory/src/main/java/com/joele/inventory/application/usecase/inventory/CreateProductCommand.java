package com.joele.inventory.application.usecase.inventory;

public record CreateProductCommand (
        String id,
        String name,
        long currentStock,
        long minStock ){}


