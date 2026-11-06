package com.joele.inventory.application.dto;

public record CreateSupplierRequestDTO(String id, String name, String contactName, String email, String phone, String address, Integer leadTimeDays, String taxNumber) {}
