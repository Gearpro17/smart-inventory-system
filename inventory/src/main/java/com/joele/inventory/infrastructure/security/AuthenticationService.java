package com.joele.inventory.infrastructure.security;

import com.joele.inventory.application.dto.CreateUserRequestDTO;
import com.joele.inventory.application.dto.LoginRequestDTO;
import com.joele.inventory.application.dto.TokenResponseDTO;
import com.joele.inventory.application.dto.UserResponseDTO;
import com.joele.inventory.domain.security.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthenticationService {
    private final UserAccountRepository users;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokens;
    private final long expirationSeconds;

    public AuthenticationService(UserAccountRepository users, PasswordEncoder passwordEncoder, JwtTokenService tokens,
                                 @Value("${inventory.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokens = tokens;
        this.expirationSeconds = expirationSeconds;
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

    private TokenResponseDTO tokenResponse(UserAccountEntity user) {
        return new TokenResponseDTO(tokens.issueToken(user), "Bearer", expirationSeconds, user.getUsername(), user.getRole().name());
    }

    public static String normalizeUsername(String username) { return username.trim().toLowerCase(Locale.ROOT); }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException() { super("Invalid username or password"); }
    }
}
