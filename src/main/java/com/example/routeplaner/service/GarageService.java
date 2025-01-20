package com.example.routeplaner.service;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.model.User;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.repository.GarageRepository;
import com.example.routeplaner.repository.UserRepository;
import com.example.routeplaner.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarageService {

    @Autowired
    private GarageRepository garageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Garage saveGarage(Garage garage, Long userId) {
        User user = userRepository.findById(userId).get();
        garage.setUser(user);
        return garageRepository.save(garage);
    }

    public List<Garage> getAllGarages(Long userId) {
        return garageRepository.findByUserId(userId);
    }

    public Garage getGarageById(Long id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garage not found with ID: " + id));
    }

    public void deleteGarage(Long id) {
        garageRepository.deleteById(id);
    }

    public Garage assignVehicleToGarage(Long garageId, Long vehicleId) {
        Garage garage = garageRepository.findById(garageId)
                .orElseThrow(() -> new IllegalArgumentException("Garage not found"));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        // Dodaj pojazd do listy pojazdów garażu
        vehicle.setGarage(garage);
        garage.getVehicles().add(vehicle);
        vehicleRepository.save(vehicle);
        return garageRepository.save(garage);
    }

    public Garage removeVehicleFromGarage(Long garageId, Long vehicleId) {
        Garage garage = garageRepository.findById(garageId)
                .orElseThrow(() -> new IllegalArgumentException("Garage not found"));

        // Usuń pojazd z listy pojazdów garażu
        garage.getVehicles().removeIf(vehicle -> vehicle.getId().equals(vehicleId));
        return garageRepository.save(garage);
    }
}
