package com.example.routeplaner.optimization.model;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import org.optaplanner.core.api.domain.solution.*;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;


import java.util.List;

@PlanningSolution
public class RoutePlan {

    @PlanningEntityCollectionProperty
    private List<Vehicle> vehicleList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "warehouseRange")
    private List<Warehouse> warehouseList;


    private List<Customer> customerList;
    private List<Garage> garageList;

    @PlanningScore
    private HardSoftScore score;

    public List<Garage> getGarageList() {
        return garageList;
    }

    public void setGarageList(List<Garage> garageList) {
        this.garageList = garageList;
    }

    @ValueRangeProvider(id = "vehicleRange")
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<Warehouse> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<Warehouse> warehouseList) {
        this.warehouseList = warehouseList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }
}