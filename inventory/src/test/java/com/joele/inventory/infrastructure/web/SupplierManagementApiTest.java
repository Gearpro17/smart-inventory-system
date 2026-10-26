package com.joele.inventory.infrastructure.web;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.application.usecase.supplier.AssignSupplierToProductUseCase;
import com.joele.inventory.application.usecase.supplier.CreateSupplierUseCase;
import com.joele.inventory.application.usecase.supplier.GetSupplierUseCase;
import com.joele.inventory.application.usecase.supplier.ListSuppliersUseCase;
import com.joele.inventory.application.usecase.supplier.RemoveSupplierFromProductUseCase;
import com.joele.inventory.application.usecase.supplier.UpdateSupplierUseCase;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.domain.inventory.Supplier;
import com.joele.inventory.infrastructure.web.controller.ProductSupplierController;
import com.joele.inventory.infrastructure.web.controller.SupplierController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.nullValue;

class SupplierManagementApiTest {
    private final InMemorySupplierRepository suppliers = new InMemorySupplierRepository();
    private final InMemoryProductRepository products = new InMemoryProductRepository();
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        var supplierController = new SupplierController(new CreateSupplierUseCase(suppliers),
                new GetSupplierUseCase(suppliers), new ListSuppliersUseCase(suppliers), new UpdateSupplierUseCase(suppliers));
        var productSupplierController = new ProductSupplierController(
                new AssignSupplierToProductUseCase(products, suppliers), new RemoveSupplierFromProductUseCase(products));
        mvc = MockMvcBuilders.standaloneSetup(supplierController, productSupplierController)
                .setControllerAdvice(new ApiExceptionHandler()).build();
    }

    @Test
    void supplierCrudAndProductAssociationAreAvailableThroughTheApi() throws Exception {
        String suffix = UUID.randomUUID().toString();
        String supplierId = "supplier-" + suffix;
        String productId = "product-" + suffix;

        mvc.perform(post("/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"%s\",\"name\":\"Acme Supply\"}".formatted(supplierId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(supplierId))
                .andExpect(jsonPath("$.name").value("Acme Supply"));

        mvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(supplierId)));

        mvc.perform(get("/suppliers/{supplierId}", supplierId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme Supply"));

        mvc.perform(put("/suppliers/{supplierId}", supplierId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme Wholesale\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme Wholesale"));

        products.save(new Product(productId, "Widget", 4, 2));
        mvc.perform(put("/products/{productId}/supplier", productId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"supplierId\":\"%s\"}".formatted(supplierId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplierId").value(supplierId));
        org.junit.jupiter.api.Assertions.assertEquals(supplierId, products.findById(productId).orElseThrow().getSupplierId());

        mvc.perform(delete("/products/{productId}/supplier", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplierId").value(nullValue()));
    }

    @Test
    void invalidDataDuplicatesAndMissingReferencesReturnExpectedErrors() throws Exception {
        String duplicateId = "duplicate-" + UUID.randomUUID();
        mvc.perform(post("/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"%s\",\"name\":\"Acme\"}".formatted(duplicateId)))
                .andExpect(status().isCreated());
        mvc.perform(post("/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"%s\",\"name\":\"Other\"}".formatted(duplicateId)))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/suppliers").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"invalid\",\"name\":\" \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/suppliers/{id}", "missing-" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
        mvc.perform(put("/suppliers/{id}", "missing-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"New name\"}"))
                .andExpect(status().isNotFound());

        String productId = "product-" + UUID.randomUUID();
        products.save(new Product(productId, "Widget", 1, 0));
        mvc.perform(put("/products/{productId}/supplier", productId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"supplierId\":\"missing-%s\"}".formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound());
        mvc.perform(put("/products/{productId}/supplier", "missing-" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"supplierId\":\"%s\"}".formatted(duplicateId)))
                .andExpect(status().isNotFound());
    }

    private static class InMemorySupplierRepository implements SupplierRepository {
        private final Map<String, Supplier> data = new LinkedHashMap<>();
        @Override public Optional<Supplier> findById(String id) { return Optional.ofNullable(data.get(id)); }
        @Override public List<Supplier> findAll() { return new ArrayList<>(data.values()); }
        @Override public Supplier save(Supplier supplier) { data.put(supplier.getId(), supplier); return supplier; }
    }

    private static class InMemoryProductRepository implements ProductRepository {
        private final Map<String, Product> data = new LinkedHashMap<>();
        @Override public Optional<Product> findById(String id) { return Optional.ofNullable(data.get(id)); }
        @Override public List<Product> findAll() { return new ArrayList<>(data.values()); }
        @Override public List<Product> findLowStock() { return data.values().stream().filter(Product::isLowStock).toList(); }
        @Override public Product save(Product product) { data.put(product.getId(), product); return product; }
    }
}
