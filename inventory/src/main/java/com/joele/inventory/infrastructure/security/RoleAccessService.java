package com.joele.inventory.infrastructure.security;

import com.joele.inventory.domain.security.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class RoleAccessService {
    public static final List<String> PERMISSIONS = List.of("INVENTORY_READ", "PRODUCT_CREATE", "MOVEMENT_READ", "MOVEMENT_WRITE", "SUPPLIER_READ", "SUPPLIER_MANAGE", "REORDER_READ", "USER_MANAGE");
    private final RolePermissionRepository permissions;
    public RoleAccessService(RolePermissionRepository permissions) { this.permissions = permissions; }

    public boolean has(String role, String permission) {
        if (!PERMISSIONS.contains(permission)) return false;
        if (UserRole.ADMIN.name().equals(role)) return true;
        return permissions.findByRoleAndPermissionCode(role, permission).map(RolePermissionEntity::isEnabled).orElseGet(() -> defaults(role).contains(permission));
    }
    public Set<String> forRole(String role) {
        if (UserRole.ADMIN.name().equals(role)) return new LinkedHashSet<>(PERMISSIONS);
        Set<String> enabled = new LinkedHashSet<>(defaults(role));
        permissions.findByRole(role).forEach(item -> { if (item.isEnabled()) enabled.add(item.getPermissionCode()); else enabled.remove(item.getPermissionCode()); });
        return enabled;
    }
    @Transactional public void update(String role, List<String> enabled) {
        if (UserRole.ADMIN.name().equals(role)) throw new IllegalArgumentException("Admin permissions are fixed and cannot be changed");
        for (String permission : PERMISSIONS) {
            boolean value = enabled.contains(permission);
            RolePermissionEntity item = permissions.findByRoleAndPermissionCode(role, permission).orElseGet(() -> new RolePermissionEntity(role, permission, value));
            item.setEnabled(value);
            permissions.save(item);
        }
    }
    private Set<String> defaults(String role) {
        return switch (role) {
            case "ADMIN" -> new LinkedHashSet<>(PERMISSIONS);
            case "INVENTORY_MANAGER" -> new LinkedHashSet<>(List.of("INVENTORY_READ", "MOVEMENT_READ", "MOVEMENT_WRITE", "SUPPLIER_READ", "SUPPLIER_MANAGE", "REORDER_READ"));
            case "VIEWER" -> new LinkedHashSet<>(List.of("INVENTORY_READ", "MOVEMENT_READ", "SUPPLIER_READ", "REORDER_READ"));
            default -> new LinkedHashSet<>();
        };
    }
}
