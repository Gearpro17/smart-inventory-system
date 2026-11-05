package com.joele.inventory.infrastructure.security;

import com.joele.inventory.domain.security.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class BootstrapAdminConfiguration {
    @Bean
    ApplicationRunner bootstrapAdmin(UserAccountRepository users, PasswordEncoder passwordEncoder,
                                     @Value("${inventory.security.bootstrap.username:}") String username,
                                     @Value("${inventory.security.bootstrap.password:}") String password) {
        return args -> {
            if (users.count() != 0 || username.isBlank() || password.isBlank()) return;
            if (username.trim().length() > 80) throw new IllegalStateException("Bootstrap admin username cannot exceed 80 characters");
            if (password.length() < 12) throw new IllegalStateException("Bootstrap admin password must contain at least 12 characters");
            String normalizedUsername = AuthenticationService.normalizeUsername(username);
            users.save(new UserAccountEntity(normalizedUsername, passwordEncoder.encode(password), UserRole.ADMIN));
        };
    }
}
