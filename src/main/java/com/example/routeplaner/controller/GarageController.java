package com.example.routeplaner.controller;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.service.GarageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/garages")
public class GarageController {

    @Autowired
    private GarageService garageService;

    @PostMapping
    public ResponseEntity<Garage> addGarage(@RequestBody Garage garage) {
        Garage savedGarage = garageService.saveGarage(garage);
        return ResponseEntity.ok(savedGarage);
    }

    @GetMapping
    public ResponseEntity<List<Garage>> getAllGarages() {
        List<Garage> garages = garageService.getAllGarages();
        return ResponseEntity.ok(garages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Garage> getGarageById(@PathVariable Long id) {
        Garage garage = garageService.getGarageById(id);
        return ResponseEntity.ok(garage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGarage(@PathVariable Long id) {
        garageService.deleteGarage(id);
        return ResponseEntity.noContent().build();
    }
}
