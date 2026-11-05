package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.CreateUserRequestDTO;
import com.joele.inventory.application.dto.LoginRequestDTO;
import com.joele.inventory.application.dto.TokenResponseDTO;
import com.joele.inventory.application.dto.UserResponseDTO;
import com.joele.inventory.infrastructure.security.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authentication;

    public AuthenticationController(AuthenticationService authentication) { this.authentication = authentication; }

    @PostMapping("/login")
    public TokenResponseDTO login(@RequestBody LoginRequestDTO request) { return authentication.login(request); }

    @PostMapping("/users")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody CreateUserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authentication.createUser(request));
    }
}
