package com.example.routeplaner.controller;

import com.example.routeplaner.exception.ResourceNotFoundException;
import com.example.routeplaner.model.User;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.repository.UserRepository;
import com.example.routeplaner.service.VehicleService;
import com.example.routeplaner.utils.JwtUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<?> addVehicle(@RequestBody Vehicle vehicle, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = JwtUtil.extractUserId(token);
        vehicleService .saveVehicle(vehicle, userId);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getVehicles(Authentication authentication) {
        Long userId = Long.valueOf(JwtUtil.validateToken(authentication.getCredentials().toString())); // Extract userId from token
        List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(userId);
        return ResponseEntity.ok(vehicles);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle updatedVehicle) {
//        Vehicle vehicle = vehicleService.updateVehicle(id, updatedVehicle);
//        return ResponseEntity.ok(vehicle);
//    }

}