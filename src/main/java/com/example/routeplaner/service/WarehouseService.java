package com.example.routeplaner.service;

import com.example.routeplaner.model.User;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.repository.UserRepository;
import com.example.routeplaner.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
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
        setOpenAndCloseHours(warehouse.getOpeningHours(), warehouse);
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

    private void setOpenAndCloseHours(String openingHours, Warehouse warehouse){
        if (openingHours != null) {
            String[] times = openingHours.split("-");
            int openTime = Integer.parseInt(times[0].split(":")[0]) * 60 + Integer.parseInt(times[0].split(":")[1]);
            int closeTime = Integer.parseInt(times[1].split(":")[0]) * 60 + Integer.parseInt(times[1].split(":")[1]);
            warehouse.setOpenTime(openTime);
            warehouse.setCloseTime(closeTime);
        }
    }
}
