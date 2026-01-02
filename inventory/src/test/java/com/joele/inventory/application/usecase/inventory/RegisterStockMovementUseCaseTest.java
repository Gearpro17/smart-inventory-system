package com.joele.inventory.application.usecase.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.StockMovementRepository;
import com.joele.inventory.common.DomainException;
import com.joele.inventory.common.enums.MovementType;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.StockMovement;

import static org.mockito.Mockito.*;

public class RegisterStockMovementUseCaseTest {

    private ProductRepository productRepository;
    private StockMovementRepository stockMovementRepository;
    private RegisterStockMovementUseCase useCase;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        stockMovementRepository = mock(StockMovementRepository.class);
        useCase = new RegisterStockMovementUseCase(
                productRepository,
                stockMovementRepository
        );
    }

    @Test
    void testExecute_ShouldRegisterStockMovementSuccessfully() {
        // Arrange
        var command = new RegisterStockMovementCommand(
                "product-1",
                MovementType.IN,
                10
        );

        var product = new Product(
                "product-1",
                "Sample Product",
                50,
                5
        );

        Mockito.when(productRepository.findById("product-1"))
                .thenReturn(product);

        useCase.execute(command);
        
        // Assert
        verify(productRepository, times(1)).findById("product-1");
        verify(productRepository, times(1)).save(any(Product.class));
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    @Test
    void testExcecute_ShouldThrowException_WhenProductNotFound() {
        // Arrange
        var command = new RegisterStockMovementCommand(
                "non-existent-product",
                MovementType.IN,
                10
        );

        Mockito.when(productRepository.findById("non-existent-product"))
                .thenReturn(null);

        try {
            // Act
            useCase.execute(command);
        } catch (DomainException ex) {
            // Assert
            assert(ex.getMessage().equals("Product not found"));
        }

        verify(productRepository, times(1)).findById("non-existent-product");
        verify(productRepository, never()).save(any(Product.class));
        verify(stockMovementRepository, never()).save(any(StockMovement.class));
    }
}
