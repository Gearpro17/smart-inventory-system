package com.joele.inventory.infrastructure.security;

import com.joele.inventory.application.dto.CreateUserRequestDTO;
import com.joele.inventory.application.dto.LoginRequestDTO;
import com.joele.inventory.application.dto.TokenResponseDTO;
import com.joele.inventory.application.dto.UserResponseDTO;
import com.joele.inventory.domain.security.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class AuthenticationService {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokens;
    private final RoleAccessService roleAccess;
    private final long expirationSeconds;

    public AuthenticationService(UserAccountRepository users, PasswordEncoder passwordEncoder, JwtTokenService tokens,
                                 @Value("${inventory.security.jwt.expiration-seconds:3600}") long expirationSeconds, RoleAccessService roleAccess) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
        this.expirationSeconds = expirationSeconds;
        this.roleAccess = roleAccess;
    }

    public TokenResponseDTO login(LoginRequestDTO request) {
        if (request == null || request.username() == null || request.password() == null) {
            throw new InvalidCredentialsException();
        }
        UserAccountEntity user = users.findById(normalizeUsername(request.username()))
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) throw new InvalidCredentialsException();
        return tokenResponse(user);
    }

    public UserResponseDTO createUser(CreateUserRequestDTO request) {
        if (request == null || request.username() == null || request.username().isBlank()
                || request.username().length() > 80 || request.password() == null || request.password().length() < 12
                || request.role() == null) {
            throw new IllegalArgumentException("Username, a password of at least 12 characters, and role are required");
        }
        String username = normalizeUsername(request.username());
        if (users.existsById(username)) throw new IllegalArgumentException("Username is already in use");
        UserAccountEntity user = users.save(new UserAccountEntity(username, passwordEncoder.encode(request.password()), request.role()));
        return new UserResponseDTO(user.getUsername(), user.getRole().name());
    }

    public java.util.List<UserResponseDTO> listUsers() { return users.findAll().stream().map(u -> new UserResponseDTO(u.getUsername(), u.getRole().name())).toList(); }
    @Transactional
    public void deleteUser(String username, String actor) {
        String normalized = normalizeUsername(username);
        if (normalized.equals(normalizeUsername(actor))) throw new IllegalArgumentException("You cannot delete your own account");
        UserAccountEntity target = users.findById(normalized).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (target.getRole() == UserRole.ADMIN && users.findAllByRoleForUpdate(UserRole.ADMIN).size() <= 1) {
            throw new IllegalArgumentException("The last admin account cannot be deleted");
        }
        users.delete(target);
    }
    public java.util.List<com.joele.inventory.application.dto.RoleAccessDTO> listRoles() {
        return java.util.Arrays.stream(UserRole.values()).map(role -> new com.joele.inventory.application.dto.RoleAccessDTO(role.name(), roleAccess.forRole(role.name()).stream().sorted().toList())).toList();
    }
    public void updateRole(String role, java.util.List<String> permissions) {
        try { UserRole.valueOf(role); } catch (RuntimeException ex) { throw new IllegalArgumentException("Unknown role"); }
        if (UserRole.ADMIN.name().equals(role)) throw new IllegalArgumentException("Admin permissions are fixed and cannot be changed");
        if (permissions == null || !RoleAccessService.PERMISSIONS.containsAll(permissions)) throw new IllegalArgumentException("Unknown permission");
        roleAccess.update(role, permissions);
    }

    private TokenResponseDTO tokenResponse(UserAccountEntity user) {
        return new TokenResponseDTO(tokens.issueToken(user), "Bearer", expirationSeconds, user.getUsername(), user.getRole().name());
    }

    public static String normalizeUsername(String username) { return username.trim().toLowerCase(Locale.ROOT); }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() { super("Invalid username or password"); }
    }
}
