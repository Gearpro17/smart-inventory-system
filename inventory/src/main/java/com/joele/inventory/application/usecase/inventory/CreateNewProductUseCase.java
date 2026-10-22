package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.application.port.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateNewProductUseCase {
    private final ProductRepository productRepository;

    public CreateNewProductUseCase(ProductRepository productRepository) {
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
            throw new IllegalArgumentException("Product with ID " + command.id() + " already exists");
        }
    }
}
