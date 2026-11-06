package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.CreateSupplierRequestDTO;
import com.joele.inventory.application.dto.SupplierResponseDTO;
import com.joele.inventory.application.dto.UpdateSupplierRequestDTO;
import com.joele.inventory.application.usecase.supplier.CreateSupplierUseCase;
import com.joele.inventory.application.usecase.supplier.GetSupplierUseCase;
import com.joele.inventory.application.usecase.supplier.ListSuppliersUseCase;
import com.joele.inventory.application.usecase.supplier.UpdateSupplierUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    private final CreateSupplierUseCase createSupplier;
    private final GetSupplierUseCase getSupplier;
    private final ListSuppliersUseCase listSuppliers;
    private final UpdateSupplierUseCase updateSupplier;

    public SupplierController(CreateSupplierUseCase createSupplier, GetSupplierUseCase getSupplier,
                              ListSuppliersUseCase listSuppliers, UpdateSupplierUseCase updateSupplier) {
        this.createSupplier = createSupplier;
        this.getSupplier = getSupplier;
        this.listSuppliers = listSuppliers;
        this.updateSupplier = updateSupplier;
    }

    @PostMapping
    public ResponseEntity<SupplierResponseDTO> create(@RequestBody CreateSupplierRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Supplier ID and name are required");
        var supplier = createSupplier.execute(request.id(), request.name(), request.contactName(), request.email(), request.phone(), request.address(), request.leadTimeDays(), request.taxNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(SupplierResponseDTO.from(supplier));
    }

    @GetMapping
    public List<SupplierResponseDTO> list() {
        return listSuppliers.execute().stream().map(SupplierResponseDTO::from).toList();
    }

    @GetMapping("/{supplierId}")
    public SupplierResponseDTO get(@PathVariable String supplierId) {
        return SupplierResponseDTO.from(getSupplier.execute(supplierId));
    }

    @PutMapping("/{supplierId}")
    public SupplierResponseDTO update(@PathVariable String supplierId, @RequestBody UpdateSupplierRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("Supplier name is required");
        return SupplierResponseDTO.from(updateSupplier.execute(supplierId, request.name(), request.contactName(), request.email(), request.phone(), request.address(), request.leadTimeDays(), request.taxNumber()));
    }
}
