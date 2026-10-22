package com.joele.inventory.infrastructure.persistence.stockmovement;

import org.springframework.stereotype.Repository;
import java.util.List;

import com.joele.inventory.application.port.StockMovementRepository;
import com.joele.inventory.domain.inventory.StockMovement;
import com.joele.inventory.infrastructure.persistence.repository.SpringDataStockMovementRepository;

@Repository
public class StockMovementRepositoryAdapter implements StockMovementRepository {

    private final SpringDataStockMovementRepository repository;

    public StockMovementRepositoryAdapter(SpringDataStockMovementRepository repository) {
        this.repository = repository;
    }

    @Override
    public StockMovement save(StockMovement movement) {
        StockMovementEntity entity = StockMovementMapper.toEntity(movement);
        return StockMovementMapper.toDomain(repository.save(entity));
    }

    @Override
    public List<StockMovement> findByProductId(String productId) {
        return repository.findByProductIdOrderByTimestampDescIdDesc(productId).stream()
                .map(StockMovementMapper::toDomain).toList();
    }

}
