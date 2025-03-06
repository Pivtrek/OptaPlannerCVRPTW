package com.example.routeplaner.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String type; // np. "truck", "van", "car"

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @JsonIgnoreProperties({"username", "email", "password", "hibernateLazyInitializer", "handler"})
    private User user;

    @ManyToOne
    @JoinColumn(name = "garage_id", nullable = true)
    @JsonIgnore
    @JsonIgnoreProperties({"vehicles", "hibernateLazyInitializer", "handler"})
    private Garage garage;


    @PlanningListVariable(valueRangeProviderRefs = "warehouseRange")
    @Transient
    private List<Warehouse> visitedWarehouses = new ArrayList<>();

    public int getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(int totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public int getTotalWarehouses() {
        return totalWarehouses;
    }

    public void setTotalWarehouses(int totalWarehouses) {
        this.totalWarehouses = totalWarehouses;
    }

    @Transient
    private int totalWarehouses;
    @Transient
    private int totalVehicles;

    public List<Warehouse> getVisitedWarehouses() {
        return visitedWarehouses;
    }

    public void setVisitedWarehouses(List<Warehouse> visitedWarehouses) {
        this.visitedWarehouses = visitedWarehouses;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Garage getGarage() {
        return garage;
    }

    public void setGarage(Garage garage) {
        this.garage = garage;
    }
}