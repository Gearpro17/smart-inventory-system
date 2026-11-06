package com.joele.inventory.domain.inventory;

import com.joele.inventory.common.DomainException;

public class Supplier {
    private final String id;
    private final String name;
    private final String contactName;
    private final String email;
    private final String phone;
    private final String address;
    private final Integer leadTimeDays;
    private final String taxNumber;

    public Supplier(String id, String name) {
        this(id, name, null, null, null, null, null, null);
    }

    public Supplier(String id, String name, String contactName, String email, String phone, String address, Integer leadTimeDays, String taxNumber) {
        if (id == null || id.isBlank()) throw DomainException.of("Supplier ID cannot be empty");
        if (name == null || name.isBlank()) throw DomainException.of("Supplier name cannot be empty");
        if (id.trim().length() > 100) throw DomainException.of("Supplier ID cannot exceed 100 characters");
        if (name.trim().length() > 150) throw DomainException.of("Supplier name cannot exceed 150 characters");
        this.id = id.trim();
        this.name = name.trim();
        if (leadTimeDays != null && leadTimeDays < 0) throw DomainException.of("Lead time cannot be negative");
        this.contactName = clean(contactName); this.email = clean(email); this.phone = clean(phone);
        this.address = clean(address); this.leadTimeDays = leadTimeDays; this.taxNumber = clean(taxNumber);
        validateLength(this.contactName, 150, "Contact name");
        validateLength(this.email, 254, "Email");
        validateLength(this.phone, 50, "Phone");
        validateLength(this.address, 500, "Address");
        validateLength(this.taxNumber, 100, "Tax number");
    }

    private static String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private static void validateLength(String value, int maximum, String field) {
        if (value != null && value.length() > maximum) throw DomainException.of(field + " cannot exceed " + maximum + " characters");
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getContactName() { return contactName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public String getTaxNumber() { return taxNumber; }
}
