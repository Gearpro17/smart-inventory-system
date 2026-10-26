package com.joele.inventory.infrastructure.persistence.supplier;

import com.joele.inventory.application.port.SupplierRepository;
import com.joele.inventory.domain.inventory.Supplier;
import com.joele.inventory.infrastructure.persistence.repository.SpringDataSupplierRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class SupplierRepositoryAdapter implements SupplierRepository {
    private final SpringDataSupplierRepository repository;

    public SupplierRepositoryAdapter(SpringDataSupplierRepository repository) {
        this.repository = repository;
    }

    @Override public Optional<Supplier> findById(String supplierId) {
        return repository.findById(supplierId).map(SupplierMapper::toDomain);
    }

    @Override public List<Supplier> findAll() {
        return repository.findAllByOrderByNameAscIdAsc().stream().map(SupplierMapper::toDomain).toList();
    }

    @Override public Supplier save(Supplier supplier) {
        return SupplierMapper.toDomain(repository.save(SupplierMapper.toEntity(supplier)));
    }
}
