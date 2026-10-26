package com.joele.inventory.infrastructure.persistence.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIERS")
@Getter
@Setter
public class SupplierEntity {
    @Id
    @Column(name = "ID", nullable = false, length = 100)
    private String id;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    protected SupplierEntity() {}
}
