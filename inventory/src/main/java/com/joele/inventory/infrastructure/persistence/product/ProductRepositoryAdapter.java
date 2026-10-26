package com.joele.inventory.infrastructure.persistence.product;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.persistence.repository.SpringDataProductRepository;
import com.joele.inventory.infrastructure.persistence.supplier.SupplierEntity;

@Repository
public class ProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository repository;
    private final EntityManager entityManager;

    public ProductRepositoryAdapter(SpringDataProductRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
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
    @Transactional
    public Product save(Product product) {
        var supplier = product.getSupplierId() == null ? null : entityManager.getReference(
                SupplierEntity.class, product.getSupplierId());
        ProductEntity entity = ProductMapper.toEntity(product, supplier);
        ProductEntity savedEntity = repository.save(entity);
        return ProductMapper.toDomain(savedEntity);
    }

}
