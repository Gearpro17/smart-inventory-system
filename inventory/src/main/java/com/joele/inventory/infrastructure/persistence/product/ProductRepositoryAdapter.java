package com.joele.inventory.infrastructure.persistence.product;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.persistence.repository.SpringDataProductRepository;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository repository;

    public ProductRepositoryAdapter(SpringDataProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Product> findById(String productId) {
        return repository.findById(productId)
        .map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return repository.findAllByOrderByNameAscIdAsc().stream().map(ProductMapper::toDomain).toList();
    }

    @Override
    public List<Product> findLowStock() {
        return repository.findLowStock().stream().map(ProductMapper::toDomain).toList();
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        ProductEntity savedEntity = repository.save(entity);
        return ProductMapper.toDomain(savedEntity);
    }

}
