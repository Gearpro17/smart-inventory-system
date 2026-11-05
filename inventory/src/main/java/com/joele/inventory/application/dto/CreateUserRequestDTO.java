package com.joele.inventory.application.dto;

import com.joele.inventory.domain.security.UserRole;

public record CreateUserRequestDTO(String username, String password, UserRole role) {}
