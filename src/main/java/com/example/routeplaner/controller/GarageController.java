package com.example.routeplaner.controller;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.service.GarageService;
import com.example.routeplaner.utils.JwtUtil;
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
    public ResponseEntity<Garage> addGarage(@RequestBody Garage garage, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = JwtUtil.extractUserId(token);
        Garage savedGarage = garageService.saveGarage(garage, userId);
        return ResponseEntity.ok(savedGarage);
    }

    @GetMapping
    public ResponseEntity<List<Garage>> getAllGarages(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = JwtUtil.extractUserId(token);
        List<Garage> garages = garageService.getAllGarages(userId);
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

    @PutMapping("/{garageId}/vehicles/{vehicleId}")
    public Garage assignVehicleToGarage(@PathVariable Long garageId, @PathVariable Long vehicleId) {
        return garageService.assignVehicleToGarage(garageId, vehicleId);
    }


    @DeleteMapping("/{garageId}/vehicles/{vehicleId}")
    public Garage removeVehicleFromGarage(@PathVariable Long garageId, @PathVariable Long vehicleId) {
        return garageService.removeVehicleFromGarage(garageId, vehicleId);
    }
}
