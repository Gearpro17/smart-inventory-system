package com.joele.inventory.infrastructure.security;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions", uniqueConstraints = @UniqueConstraint(columnNames = {"role", "permission_code"}))
public class RolePermissionEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, length = 30) private String role;
    @Column(name = "permission_code", nullable = false, length = 60) private String permissionCode;
    @Column(nullable = false) private boolean enabled;
    protected RolePermissionEntity() {}
    public RolePermissionEntity(String role, String permissionCode, boolean enabled) { this.role = role; this.permissionCode = permissionCode; this.enabled = enabled; }
    public String getRole() { return role; }
    public String getPermissionCode() { return permissionCode; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
