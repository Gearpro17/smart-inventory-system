package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.CreateProductRequestDTO;
import com.joele.inventory.application.usecase.inventory.CreateNewProductUseCase;
import com.joele.inventory.application.usecase.inventory.CreateProductCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final CreateNewProductUseCase createNewProductUseCase;

    public ProductController(CreateNewProductUseCase createNewProductUseCase) {
        this.createNewProductUseCase = createNewProductUseCase;
    }

    @PostMapping("create-product")
    public ResponseEntity<Void> createProduct(@RequestBody CreateProductRequestDTO request){
        CreateProductCommand command = new CreateProductCommand(
                request.getId(),
                request.getName(),
                request.getCurrentStock(),
                request.getMinStock()
        );

        createNewProductUseCase.execute(command);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
