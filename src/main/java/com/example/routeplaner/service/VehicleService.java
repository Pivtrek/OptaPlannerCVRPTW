package com.example.routeplaner.service;

import com.example.routeplaner.model.User;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.repository.UserRepository;
import com.example.routeplaner.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private UserRepository userRepository;

    public Vehicle saveVehicle(Vehicle vehicle, Long userId) {
        User user = userRepository.findById(userId).get();
        vehicle.setUser(user);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId);
    }

    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

}
