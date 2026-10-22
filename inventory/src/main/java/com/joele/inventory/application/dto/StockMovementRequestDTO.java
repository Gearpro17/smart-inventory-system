package com.joele.inventory.application.dto;

import com.joele.inventory.common.enums.MovementType;

public record StockMovementRequestDTO(MovementType type, long quantity) { }
