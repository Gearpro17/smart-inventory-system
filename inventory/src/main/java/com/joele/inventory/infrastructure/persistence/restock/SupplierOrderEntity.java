package com.joele.inventory.infrastructure.persistence.restock;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "SUPPLIER_ORDERS") @Getter @Setter
public class SupplierOrderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name="PRODUCT_ID", nullable=false, length=100) private String productId;
    @Column(name="PRODUCT_NAME", nullable=false, length=150) private String productName;
    @Column(name="SUPPLIER_ID", nullable=false, length=100) private String supplierId;
    @Column(name="SUPPLIER_NAME", nullable=false, length=150) private String supplierName;
    @Column(nullable=false) private long quantity;
    @Column(name="ORDER_DATE", nullable=false) private LocalDateTime orderDate;
    @Column(nullable=false, length=20) private String status;
    @Column(name="MOVEMENT_ID") private Long movementId;
    public SupplierOrderEntity() {}
}
