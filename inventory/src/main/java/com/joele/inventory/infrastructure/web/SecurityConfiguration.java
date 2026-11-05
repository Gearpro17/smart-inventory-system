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
    SecurityFilterChain apiSecurity(HttpSecurity http, JwtTokenService tokens, SecurityErrorWriter errors) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(errors).accessDeniedHandler(errors))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/auth/login", "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/create-product").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/products/*/movements").hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.POST, "/suppliers").hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/suppliers/*").hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/products/*/supplier").hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/products/*/supplier").hasAnyRole("ADMIN", "INVENTORY_MANAGER")
                        .anyRequest().authenticated())
                .addFilterBefore(new JwtAuthenticationFilter(tokens), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

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
