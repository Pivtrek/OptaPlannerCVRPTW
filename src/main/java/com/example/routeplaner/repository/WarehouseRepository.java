package com.example.routeplaner.repository;

import com.example.routeplaner.model.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    // Pobierz magazyny przypisane do użytkownika
    List<Warehouse> findByUserId(Long userId);
}
