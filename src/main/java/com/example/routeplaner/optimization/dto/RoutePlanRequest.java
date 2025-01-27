package com.example.routeplaner.optimization.dto;

import java.util.List;

public class RoutePlanRequest {
    private List<Long> vehicles;
    private List<WarehouseInput> warehouses;

    public static class WarehouseInput {
        private Long id;
        private Integer load;
        private Integer serviceTime;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getServiceTime() {
            return serviceTime;
        }

        public void setServiceTime(Integer serviceTime) {
            this.serviceTime = serviceTime;
        }

        public Integer getLoad() {
            return load;
        }

        public void setLoad(Integer load) {
            this.load = load;
        }

    }

    public List<Long> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Long> vehicles) {
        this.vehicles = vehicles;
    }

    public List<WarehouseInput> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<WarehouseInput> warehouses) {
        this.warehouses = warehouses;
    }
}
