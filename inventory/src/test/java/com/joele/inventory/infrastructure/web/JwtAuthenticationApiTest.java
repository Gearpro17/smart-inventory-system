package com.joele.inventory.infrastructure.web;

import com.joele.inventory.application.port.ProductRepository;
import com.joele.inventory.domain.inventory.Product;
import com.joele.inventory.infrastructure.security.AuthenticationService;
import com.joele.inventory.infrastructure.security.UserAccountEntity;
import com.joele.inventory.infrastructure.security.UserAccountRepository;
import com.joele.inventory.domain.security.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@ActiveProfiles("dev")
class JwtAuthenticationApiTest {
    @DynamicPropertySource
    static void securityProperties(DynamicPropertyRegistry properties) {
        properties.add("inventory.security.jwt.secret", () -> Base64.getEncoder().encodeToString(new byte[32]));
    }

    @Autowired private WebApplicationContext context;
    @Autowired private UserAccountRepository users;
    @Autowired private ProductRepository products;
    @Autowired private AuthenticationService authentication;
    @Autowired private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = webAppContextSetup(context).apply(springSecurity()).build();
        users.deleteAll();
        users.save(new UserAccountEntity("admin", passwordEncoder.encode("admin-password-123"), UserRole.ADMIN));
        users.save(new UserAccountEntity("manager", passwordEncoder.encode("manager-password-123"), UserRole.INVENTORY_MANAGER));
        users.save(new UserAccountEntity("viewer", passwordEncoder.encode("viewer-password-123"), UserRole.VIEWER));
    }

    @Test
    void issuesSignedTokenAndAcceptsItForAuthorizedReads() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin-password-123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        String token = authentication.login(new com.joele.inventory.application.dto.LoginRequestDTO("admin", "admin-password-123")).accessToken();
        mvc.perform(get("/products").header("Authorization", "Bearer " + token)).andExpect(status().isOk());
    }

    @Test
    void rejectsMissingMalformedInvalidAndExpiredTokensWithConsistent401Responses() throws Exception {
        assertUnauthorized(null);
        assertUnauthorized("not-a-jwt");
        assertUnauthorized("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.invalid-signature");
        assertUnauthorized(expiredToken());
    }

    @Test
    void passwordFailureReturnsUnauthorizedAndViewerCannotMutateInventory() throws Exception {
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"wrong-password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"));

        String viewerToken = authentication.login(new com.joele.inventory.application.dto.LoginRequestDTO("viewer", "viewer-password-123")).accessToken();
        mvc.perform(post("/products/create-product").header("Authorization", "Bearer " + viewerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"forbidden-product\",\"name\":\"Forbidden\",\"currentStock\":1,\"minStock\":0}"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void managerCanRecordMovementsButCannotCreateProducts() throws Exception {
        products.save(new Product("auth-test-product", "Auth test product", 2, 0));
        String token = authentication.login(new com.joele.inventory.application.dto.LoginRequestDTO("manager", "manager-password-123")).accessToken();

        mvc.perform(post("/products/auth-test-product/movements").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"type\":\"IN\",\"quantity\":1}"))
                .andExpect(status().isCreated());
        mvc.perform(post("/products/create-product").header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"not-allowed\",\"name\":\"No\",\"currentStock\":1,\"minStock\":0}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void onlyAdminCanProvisionAccountsAndCredentialFieldsAreNeverReturned() throws Exception {
        String adminToken = authentication.login(new com.joele.inventory.application.dto.LoginRequestDTO("admin", "admin-password-123")).accessToken();
        mvc.perform(post("/auth/users").header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"new-viewer\",\"password\":\"new-viewer-password\",\"role\":\"VIEWER\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("new-viewer"))
                .andExpect(jsonPath("$.role").value("VIEWER"))
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
        assertNotEquals("new-viewer-password", users.findById("new-viewer").orElseThrow().getPasswordHash());

        mvc.perform(post("/auth/users").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"blocked-user\",\"password\":\"blocked-user-password\",\"role\":\"VIEWER\"}"))
                .andExpect(status().isUnauthorized());
    }

    private void assertUnauthorized(String token) throws Exception {
        var request = get("/products");
        if (token != null) request.header("Authorization", "Bearer " + token);
        mvc.perform(request).andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401));
    }

    private String expiredToken() {
        byte[] secret = new byte[32];
        SecretKey key = Keys.hmacShaKeyFor(secret);
        return Jwts.builder().subject("admin").claim("role", "ADMIN")
                .issuedAt(new Date(System.currentTimeMillis() - 120_000))
                .expiration(new Date(System.currentTimeMillis() - 60_000)).signWith(key).compact();
    }
}
