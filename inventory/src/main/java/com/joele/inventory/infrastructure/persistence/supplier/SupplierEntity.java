package com.joele.inventory.infrastructure.persistence.supplier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "SUPPLIERS")
@Getter
@Setter
public class SupplierEntity {
    @Id
    @Column(name = "ID", nullable = false, length = 100)
    private String id;

    @Column(name = "NAME", nullable = false, length = 150)
    private String name;

    @Column(name = "CONTACT_NAME", length = 150) private String contactName;
    @Column(name = "EMAIL", length = 254) private String email;
    @Column(name = "PHONE", length = 50) private String phone;
    @Column(name = "ADDRESS", length = 500) private String address;
    @Column(name = "LEAD_TIME_DAYS") private Integer leadTimeDays;
    @Column(name = "TAX_NUMBER", length = 100) private String taxNumber;

    protected SupplierEntity() {}
}
