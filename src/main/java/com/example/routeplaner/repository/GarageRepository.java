package com.example.routeplaner.repository;

import com.example.routeplaner.model.Garage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GarageRepository extends JpaRepository<Garage, Long> {
}
