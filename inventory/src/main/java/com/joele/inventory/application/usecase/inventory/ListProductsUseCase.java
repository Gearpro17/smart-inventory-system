package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListProductsUseCase {
    private final ProductRepository products;

    public ListProductsUseCase(ProductRepository products) { this.products = products; }

    public List<Product> execute() { return products.findAll(); }
}
