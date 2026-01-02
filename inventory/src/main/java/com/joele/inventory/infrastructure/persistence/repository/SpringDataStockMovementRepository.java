package com.joele.inventory.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.joele.inventory.infrastructure.persistence.stockmovement.StockMovementEntity;

public interface SpringDataStockMovementRepository extends JpaRepository<StockMovementEntity, Long> {

}
