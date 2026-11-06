package com.joele.inventory.infrastructure.persistence.restock;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierOrderRepository extends JpaRepository<SupplierOrderEntity, Long> {
    List<SupplierOrderEntity> findAllByOrderByOrderDateDesc();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from SupplierOrderEntity o where o.id = :id")
    Optional<SupplierOrderEntity> findByIdForUpdate(@Param("id") Long id);
}
