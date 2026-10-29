package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.StockMovementResponseDTO;
import com.joele.inventory.application.usecase.inventory.ListStockMovementsUseCase;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/movements")
public class StockMovementController {
    private final ListStockMovementsUseCase listMovements;

    public StockMovementController(ListStockMovementsUseCase listMovements) {
        this.listMovements = listMovements;
    }

    @GetMapping
    public List<StockMovementResponseDTO> listAll() {
        return listMovements.executeAll().stream().map(StockMovementResponseDTO::from).toList();
    }
}
