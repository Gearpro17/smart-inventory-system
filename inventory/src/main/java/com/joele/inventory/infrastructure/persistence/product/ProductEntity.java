package com.joele.inventory.infrastructure.persistence.product;

import org.springframework.data.relational.core.mapping.Table;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PRODUCTS")
@Setter
@Getter
public class ProductEntity {
    @Id
    private String id;

    @Column(name = "NAME", nullable = false, length = 100, unique = false)
    private String name;

    @Column(name = "CURRENT_STOCK", nullable = false)
    private long currentStock;

    @Column(name = "MIN_STOCK", nullable = false)
    private long minStock;

    protected ProductEntity() {
    }  
}
