package com.joele.inventory.infrastructure.web.controller;

import com.joele.inventory.application.dto.CreateSupplierOrderRequestDTO;
import com.joele.inventory.application.dto.SupplierOrderDTO;
import com.joele.inventory.application.usecase.supplier.SupplierOrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/supplier-orders")
public class SupplierOrderController {
    private final SupplierOrderService service;
    public SupplierOrderController(SupplierOrderService service) { this.service=service; }
    @PostMapping public org.springframework.http.ResponseEntity<SupplierOrderDTO> create(@RequestBody CreateSupplierOrderRequestDTO body) {
        if (body==null || body.productId()==null) throw new IllegalArgumentException("Product and quantity are required");
        return org.springframework.http.ResponseEntity.status(HttpStatus.CREATED).body(SupplierOrderDTO.from(service.create(body.productId(),body.quantity())));
    }
    @GetMapping public List<SupplierOrderDTO> list() { return service.list().stream().map(SupplierOrderDTO::from).toList(); }
    @PostMapping("/{id}/cancel") public SupplierOrderDTO cancel(@PathVariable Long id) { return SupplierOrderDTO.from(service.cancel(id)); }
}
