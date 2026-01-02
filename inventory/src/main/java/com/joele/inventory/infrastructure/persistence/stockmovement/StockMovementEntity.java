package com.joele.inventory.infrastructure.persistence.stockmovement;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.joele.inventory.common.enums.MovementType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "STOCK_MOVEMENTS")
@Getter
@Setter
public class StockMovementEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PRODUCT_ID", nullable = false, length = 100)
    private String productId;

    @Enumerated(EnumType.STRING)
    private MovementType type;

    @Column(name = "QUANTITY", nullable = false)
    private long quantity;

    @Column(name = "TIMESTAMP", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime timestamp;

    protected StockMovementEntity() {
    }
}
