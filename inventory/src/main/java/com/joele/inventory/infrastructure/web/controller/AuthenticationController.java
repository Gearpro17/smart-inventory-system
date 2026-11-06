package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.CreateUserRequestDTO;
import com.joele.inventory.application.dto.LoginRequestDTO;
import com.joele.inventory.application.dto.TokenResponseDTO;
import com.joele.inventory.application.dto.UserResponseDTO;
import com.joele.inventory.infrastructure.security.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.List;
import com.joele.inventory.application.dto.RoleAccessDTO;
import com.joele.inventory.application.dto.UpdateRoleAccessDTO;

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

    @GetMapping("/users") public List<UserResponseDTO> users() { return authentication.listUsers(); }
    @DeleteMapping("/users/{username}") public ResponseEntity<Void> deleteUser(@PathVariable String username, Authentication actor) {
        authentication.deleteUser(username, actor.getName()); return ResponseEntity.noContent().build();
    }
    @GetMapping("/roles") public List<RoleAccessDTO> roles() { return authentication.listRoles(); }
    @PutMapping("/roles/{role}") public ResponseEntity<Void> updateRole(@PathVariable String role, @RequestBody UpdateRoleAccessDTO request) {
        authentication.updateRole(role, request.permissions()); return ResponseEntity.noContent().build();
    }
}
