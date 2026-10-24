package com.joele.inventory.infrastructure.web;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.StockMovementRepository;
import com.joele.inventory.application.usecase.inventory.CreateNewProductUseCase;
import com.joele.inventory.application.usecase.inventory.GenerateReorderSuggestionsUseCase;
import com.joele.inventory.application.usecase.inventory.GetProductUseCase;
import com.joele.inventory.application.usecase.inventory.ListLowStockProductsUseCase;
import com.joele.inventory.application.usecase.inventory.ListProductsUseCase;
import com.joele.inventory.application.usecase.inventory.ListStockMovementsUseCase;
import com.joele.inventory.application.usecase.inventory.RegisterStockMovementUseCase;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.StockMovement;
import com.joele.inventory.infrastructure.web.controller.ProductController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReorderSuggestionsApiTest {
    private ProductRepository products;
    private InMemoryStockMovementRepository movements;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        products = new InMemoryProductRepository();
        movements = new InMemoryStockMovementRepository();
        var getProduct = new GetProductUseCase(products);
        var controller = new ProductController(
                new CreateNewProductUseCase(products),
                new ListProductsUseCase(products),
                new ListLowStockProductsUseCase(products),
                getProduct,
                new RegisterStockMovementUseCase(products, movements),
                new ListStockMovementsUseCase(getProduct, movements),
                new GenerateReorderSuggestionsUseCase(products));
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void returnsSuggestionFieldsForLowStockProduct() throws Exception {
        products.save(new Product("p-1", "Widget", 2, 5));

        mvc.perform(get("/products/reorder-suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value("p-1"))
                .andExpect(jsonPath("$[0].productName").value("Widget"))
                .andExpect(jsonPath("$[0].currentStock").value(2))
                .andExpect(jsonPath("$[0].minStock").value(5))
                .andExpect(jsonPath("$[0].targetStock").value(5))
                .andExpect(jsonPath("$[0].suggestedQuantity").value(3));
    }

    @Test
    void returnsAnEmptyArrayWhenThereAreNoSuggestions() throws Exception {
        mvc.perform(get("/products/reorder-suggestions"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    private static class InMemoryProductRepository implements ProductRepository {
        private final List<Product> products = new ArrayList<>();

        @Override public Optional<Product> findById(String productId) {
            return products.stream().filter(product -> product.getId().equals(productId)).findFirst();
        }
        @Override public List<Product> findAll() { return List.copyOf(products); }
        @Override public List<Product> findLowStock() {
            return products.stream().filter(Product::isLowStock).toList();
        }
        @Override public Product save(Product product) {
            products.removeIf(existing -> existing.getId().equals(product.getId()));
            products.add(product);
            return product;
        }
    }

    private static class InMemoryStockMovementRepository implements StockMovementRepository {
        private final List<StockMovement> movements = new ArrayList<>();

        @Override public StockMovement save(StockMovement movement) { movements.add(movement); return movement; }
        @Override public List<StockMovement> findByProductId(String productId) {
            return movements.stream().filter(movement -> movement.getProductId().equals(productId)).toList();
        }
    }
}
