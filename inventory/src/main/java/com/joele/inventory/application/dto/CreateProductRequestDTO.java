package com.joele.inventory.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class CreateProductRequestDTO {
    private String id;
    private String name;
    private Long currentStock;
    private Long minStock;
}
