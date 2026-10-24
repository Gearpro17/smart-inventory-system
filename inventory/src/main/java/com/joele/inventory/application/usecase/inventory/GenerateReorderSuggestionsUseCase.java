package com.joele.inventory.application.usecase.inventory;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.ReorderPolicy;
import com.joele.inventory.domain.inventory.ReorderSuggestion;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GenerateReorderSuggestionsUseCase {
    private final ProductRepository products;
    private final ReorderPolicy policy;

    public GenerateReorderSuggestionsUseCase(ProductRepository products) {
        this.products = products;
        this.policy = new ReorderPolicy();
    }

    public List<ReorderSuggestion> execute() {
        return products.findLowStock().stream()
                .map(policy::suggestFor)
                .flatMap(java.util.Optional::stream)
                .toList();
    }
}
