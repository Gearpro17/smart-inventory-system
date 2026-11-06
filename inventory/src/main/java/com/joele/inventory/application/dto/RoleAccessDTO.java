package com.joele.inventory.application.dto;
import java.util.List;
public record RoleAccessDTO(String role, List<String> permissions) {}
