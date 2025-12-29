package com.joele.inventory.common.enums;

public enum MovementType {
    IN,
    OUT;

    public boolean isInbound() {
        return this == IN;
    }

    public boolean isOutbound() {
        return this == OUT;
    }
}
