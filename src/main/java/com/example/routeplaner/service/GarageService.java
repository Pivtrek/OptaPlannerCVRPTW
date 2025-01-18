package com.example.routeplaner.service;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.repository.GarageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarageService {

    @Autowired
    private GarageRepository garageRepository;

    public Garage saveGarage(Garage garage) {
        return garageRepository.save(garage);
    }

    public List<Garage> getAllGarages() {
        return garageRepository.findAll();
    }

    public Garage getGarageById(Long id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Garage not found with ID: " + id));
    }

    public void deleteGarage(Long id) {
        garageRepository.deleteById(id);
    }
}
