package com.example.routeplaner.optimization.model;

import com.example.routeplaner.model.Vehicle;
import org.optaplanner.core.api.domain.solution.PlanningEntityProperty;
import org.optaplanner.core.api.domain.solution.PlanningSolution;


import java.util.List;

@PlanningSolution
public class RoutePlan {
    private List<Vehicle> vehicleList;
    private List<Customer> customerList;

    // Getters and Setters
    public List<Vehicle> getVehicleList() {
        return vehicleList;
    }

    public void setVehicleList(List<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<Customer> customerList) {
        this.customerList = customerList;
    }
}