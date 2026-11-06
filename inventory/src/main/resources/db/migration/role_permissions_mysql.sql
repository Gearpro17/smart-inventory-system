CREATE TABLE role_permissions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    role VARCHAR(30) NOT NULL,
    permission_code VARCHAR(60) NOT NULL,
    enabled BOOLEAN NOT NULL,
    CONSTRAINT uq_role_permission UNIQUE (role, permission_code)
);
