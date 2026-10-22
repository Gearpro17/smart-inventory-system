package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.application.port.StockMovementRepository;
import com.joele.inventory.domain.inventory.StockMovement;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListStockMovementsUseCase {
    private final GetProductUseCase getProduct;
    private final StockMovementRepository movements;

    public ListStockMovementsUseCase(GetProductUseCase getProduct, StockMovementRepository movements) {
        this.getProduct = getProduct;
        this.movements = movements;
    }

    public List<StockMovement> execute(String productId) {
        getProduct.execute(productId);
        return movements.findByProductId(productId);
    }
}
