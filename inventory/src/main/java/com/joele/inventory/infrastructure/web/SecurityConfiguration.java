package com.joele.inventory.infrastructure.web;

import com.joele.inventory.infrastructure.security.JwtAuthenticationFilter;
import com.joele.inventory.infrastructure.security.JwtTokenService;
import com.joele.inventory.infrastructure.security.SecurityErrorWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfiguration {
    @Bean
    SecurityFilterChain apiSecurity(HttpSecurity http, JwtTokenService tokens, SecurityErrorWriter errors, com.joele.inventory.infrastructure.security.RoleAccessService roleAccess, com.joele.inventory.infrastructure.security.UserAccountRepository users) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(errors).accessDeniedHandler(errors))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/actuator/health").permitAll()
                        .anyRequest().access((authenticationSupplier, context) -> {
                            var currentAuthentication = authenticationSupplier.get();
                            if (currentAuthentication == null || !currentAuthentication.isAuthenticated() || currentAuthentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) return new org.springframework.security.authorization.AuthorizationDecision(false);
                            String permission = requiredPermission(context.getRequest().getMethod(), context.getRequest().getRequestURI());
                            if (permission == null) return new org.springframework.security.authorization.AuthorizationDecision(true);
                            String role = currentAuthentication.getAuthorities().stream().map(a -> a.getAuthority()).filter(a -> a.startsWith("ROLE_")).map(a -> a.substring(5)).findFirst().orElse("");
                            return new org.springframework.security.authorization.AuthorizationDecision(roleAccess.has(role, permission));
                        }))
                .addFilterBefore(new JwtAuthenticationFilter(tokens, users), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static String requiredPermission(String method, String path) {
        if (path.equals("/auth/users") || path.startsWith("/auth/users/")) return "USER_MANAGE";
        if (path.startsWith("/auth/roles") && !method.equals("GET")) return "USER_MANAGE";
        if (path.equals("/products/create-product") && method.equals("POST")) return "PRODUCT_CREATE";
        if (path.matches("/products/[^/]+/movements")) return method.equals("POST") ? "MOVEMENT_WRITE" : "MOVEMENT_READ";
        if (path.equals("/movements")) return "MOVEMENT_READ";
        if (path.equals("/suppliers") || path.startsWith("/suppliers/")) return method.equals("GET") ? "SUPPLIER_READ" : "SUPPLIER_MANAGE";
        if (path.endsWith("/supplier") && path.startsWith("/products/")) return "SUPPLIER_MANAGE";
        if (path.equals("/products/reorder-suggestions")) return "REORDER_READ";
        if (path.equals("/supplier-orders")) return method.equals("GET") ? "REORDER_READ" : "SUPPLIER_MANAGE";
        if (path.startsWith("/supplier-orders/")) return "SUPPLIER_MANAGE";
        if (path.startsWith("/products")) return "INVENTORY_READ";
        return null;
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    UserDetailsService userDetailsService(com.joele.inventory.infrastructure.security.UserAccountRepository users) {
        return username -> users.findById(com.joele.inventory.infrastructure.security.AuthenticationService.normalizeUsername(username))
                .map(account -> User.withUsername(account.getUsername())
                        .password(account.getPasswordHash())
                        .roles(account.getRole().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    SecurityErrorWriter securityErrorWriter() { return new SecurityErrorWriter(); }

    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${inventory.security.allowed-origins:http://localhost:4200,http://127.0.0.1:4200}") String configuredOrigins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(configuredOrigins.split(",")).stream().map(String::trim).toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
