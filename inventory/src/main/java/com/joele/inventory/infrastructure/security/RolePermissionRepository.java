package com.joele.inventory.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RolePermissionRepository extends JpaRepository<RolePermissionEntity, Long> {
    List<RolePermissionEntity> findByRole(String role);
    Optional<RolePermissionEntity> findByRoleAndPermissionCode(String role, String permissionCode);
}
