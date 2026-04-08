package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.persistence.product.ProductRepositoryAdapter;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateNewProductUseCase {
    private final ProductRepositoryAdapter productRepository;

    public CreateNewProductUseCase(ProductRepositoryAdapter productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void execute(CreateProductCommand command){

        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        // Validate no duplicate products
        if(productRepository.findById(command.id()).isEmpty()) {

            Product product = new Product(
                    command.id(),
                    command.name(),
                    command.currentStock(),
                    command.minStock()
            );

            // Persist
            productRepository.save(product);
        } else {
            throw new IllegalArgumentException("Product with id" +  command.id() + "already exists");
        }
    }
}
