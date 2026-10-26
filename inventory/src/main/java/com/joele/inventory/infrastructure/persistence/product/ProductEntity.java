package com.joele.inventory.infrastructure.persistence.product;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @jakarta.persistence.ForeignKey(name = "FK_PRODUCTS_SUPPLIER"))
    private com.joele.inventory.infrastructure.persistence.supplier.SupplierEntity supplier;

    protected ProductEntity() {
    }  
}
