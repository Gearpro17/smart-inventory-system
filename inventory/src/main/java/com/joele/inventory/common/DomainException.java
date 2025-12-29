package com.joele.inventory.common;

public class DomainException extends RuntimeException{
    public DomainException() {
        super();
    }

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainException(Throwable cause) {
        super(cause);
    }

    public static DomainException of(String message) {
        return new DomainException(message);
    }
}
