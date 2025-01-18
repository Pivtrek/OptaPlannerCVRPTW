package com.example.routeplaner.controller;

import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<Warehouse> addWarehouse(@RequestBody Warehouse warehouse) {
        Warehouse savedWarehouse = warehouseService.saveWarehouse(warehouse);
        return ResponseEntity.ok(savedWarehouse);
    }

    @GetMapping
    public ResponseEntity<List<Warehouse>> getWarehousesByUserId(@RequestParam Long userId) {
        List<Warehouse> warehouses = warehouseService.getWarehousesByUserId(userId);
        return ResponseEntity.ok(warehouses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Long id, @RequestBody Warehouse updatedWarehouse) {
        Warehouse warehouse = warehouseService.updateWarehouse(id, updatedWarehouse);
        return ResponseEntity.ok(warehouse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable Long id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
