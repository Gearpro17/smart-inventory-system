package com.joele.inventory.application.dto;

public record TokenResponseDTO(String accessToken, String tokenType, long expiresIn, String username, String role) {}
