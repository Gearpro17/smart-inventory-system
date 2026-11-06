package com.joele.inventory.infrastructure.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select account from UserAccountEntity account where account.role = :role order by account.username")
    List<UserAccountEntity> findAllByRoleForUpdate(@Param("role") com.joele.inventory.domain.security.UserRole role);
}
