package com.joele.inventory.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtTokenService {
    private final SecretKey signingKey;
    private final long expirationSeconds;

    public JwtTokenService(@Value("${inventory.security.jwt.secret}") String encodedSecret,
                           @Value("${inventory.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        byte[] secret;
        try {
            secret = Base64.getDecoder().decode(encodedSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT secret must be valid Base64", exception);
        }
        if (secret.length < 32) throw new IllegalStateException("JWT secret must contain at least 256 bits");
        if (expirationSeconds < 60) throw new IllegalStateException("JWT expiration must be at least 60 seconds");
        this.signingKey = Keys.hmacShaKeyFor(secret);
        this.expirationSeconds = expirationSeconds;
    }

    public String issueToken(UserAccountEntity user) {
        Instant issuedAt = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(issuedAt.plusSeconds(expirationSeconds)))
                .signWith(signingKey)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }
}
