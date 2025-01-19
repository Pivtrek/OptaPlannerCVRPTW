package com.example.routeplaner.service;

import com.example.routeplaner.model.User;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.repository.UserRepository;
import com.example.routeplaner.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private UserRepository userRepository;

    public Warehouse saveWarehouse(Warehouse warehouse, Long userId) {
        User user = userRepository.findById(userId).get();
        warehouse.setUser(user);
        return warehouseRepository.save(warehouse);
    }

    public List<Warehouse> getWarehousesByUserId(Long userId) {
        return warehouseRepository.findByUserId(userId);
    }

    public Warehouse updateWarehouse(Long id, Warehouse updatedWarehouse) {
        return warehouseRepository.findById(id).map(warehouse -> {
            warehouse.setName(updatedWarehouse.getName());
            warehouse.setAddress(updatedWarehouse.getAddress());
            warehouse.setLatitude(updatedWarehouse.getLatitude());
            warehouse.setLongitude(updatedWarehouse.getLongitude());
            warehouse.setOpeningHours(updatedWarehouse.getOpeningHours());
            return warehouseRepository.save(warehouse);
        }).orElseThrow(() -> new RuntimeException("Warehouse not found with ID: " + id));
    }

    public void deleteWarehouse(Long id) {
        if (!warehouseRepository.existsById(id)) {
            throw new RuntimeException("Warehouse not found with ID: " + id);
        }
        warehouseRepository.deleteById(id);
    }
}
