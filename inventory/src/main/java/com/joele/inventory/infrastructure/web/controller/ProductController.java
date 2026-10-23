package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.CreateProductRequestDTO;
import com.joele.inventory.application.dto.ProductResponseDTO;
import com.joele.inventory.application.dto.StockMovementRequestDTO;
import com.joele.inventory.application.dto.StockMovementResponseDTO;
import com.joele.inventory.application.usecase.inventory.CreateNewProductUseCase;
import com.joele.inventory.application.usecase.inventory.CreateProductCommand;
import com.joele.inventory.application.usecase.inventory.GetProductUseCase;
import com.joele.inventory.application.usecase.inventory.ListProductsUseCase;
import com.joele.inventory.application.usecase.inventory.ListLowStockProductsUseCase;
import com.joele.inventory.application.usecase.inventory.ListStockMovementsUseCase;
import com.joele.inventory.application.usecase.inventory.RegisterStockMovementCommand;
import com.joele.inventory.application.usecase.inventory.RegisterStockMovementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final CreateNewProductUseCase createProduct;
    private final ListProductsUseCase listProducts;
    private final ListLowStockProductsUseCase listLowStockProducts;
    private final GetProductUseCase getProduct;
    private final RegisterStockMovementUseCase registerMovement;
    private final ListStockMovementsUseCase listMovements;

    public ProductController(CreateNewProductUseCase createProduct, ListProductsUseCase listProducts,
                             ListLowStockProductsUseCase listLowStockProducts,
                             GetProductUseCase getProduct, RegisterStockMovementUseCase registerMovement,
                             ListStockMovementsUseCase listMovements) {
        this.createProduct = createProduct;
        this.listProducts = listProducts;
        this.listLowStockProducts = listLowStockProducts;
        this.getProduct = getProduct;
        this.registerMovement = registerMovement;
        this.listMovements = listMovements;
    }

    @GetMapping
    public List<ProductResponseDTO> list() {
        return listProducts.execute().stream().map(ProductResponseDTO::from).toList();
    }

    @GetMapping("/low-stock")
    public List<ProductResponseDTO> listLowStock() {
        return listLowStockProducts.execute().stream().map(ProductResponseDTO::from).toList();
    }

    @GetMapping("/{productId}")
    public ProductResponseDTO get(@PathVariable String productId) {
        return ProductResponseDTO.from(getProduct.execute(productId));
    }

    @PostMapping("/{productId}/movements")
    public ResponseEntity<StockMovementResponseDTO> registerMovement(@PathVariable String productId,
                                                                      @RequestBody StockMovementRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Request body is required");
        var movement = registerMovement.execute(new RegisterStockMovementCommand(productId, request.type(), request.quantity()));
        return ResponseEntity.status(HttpStatus.CREATED).body(StockMovementResponseDTO.from(movement));
    }

    @GetMapping("/{productId}/movements")
    public List<StockMovementResponseDTO> movements(@PathVariable String productId) {
        return listMovements.execute(productId).stream().map(StockMovementResponseDTO::from).toList();
    }

    @PostMapping("/create-product")
    public ResponseEntity<Void> create(@RequestBody CreateProductRequestDTO request) {
        if (request == null || request.getCurrentStock() == null || request.getMinStock() == null) {
            throw new IllegalArgumentException("Product ID, name, currentStock, and minStock are required");
        }
        createProduct.execute(new CreateProductCommand(request.getId(), request.getName(), request.getCurrentStock(), request.getMinStock()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
