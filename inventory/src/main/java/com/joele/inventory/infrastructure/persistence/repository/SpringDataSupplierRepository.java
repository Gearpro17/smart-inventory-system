package com.joele.inventory.infrastructure.persistence.repository;

import com.joele.inventory.infrastructure.persistence.supplier.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataSupplierRepository extends JpaRepository<SupplierEntity, String> {
    List<SupplierEntity> findAllByOrderByNameAscIdAsc();
}
