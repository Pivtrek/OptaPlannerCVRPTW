package com.example.routeplaner.optimization.rules;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.optimization.model.RoutePlan;
import lombok.extern.slf4j.Slf4j;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.*;

import java.util.List;

@Slf4j
public class RoutePlanConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                minimizeDistance(constraintFactory),
                vehicleOverCapacity(constraintFactory),
                ensureBalancedAssignment(constraintFactory),
                balanceWorkload(constraintFactory),
                limitMaxWarehouses(constraintFactory),
                enforceVehicleCapacity(constraintFactory),
                enforceTimeWindows(constraintFactory),
        };
    }

    private Constraint balanceWorkload(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .penalize("Balance workload", HardSoftScore.ONE_SOFT, vehicle -> {
                    int totalVehicles = vehicle.getTotalVehicles();
                    int totalWarehouses = vehicle.getTotalWarehouses();
                    int averageWarehouses = totalWarehouses / totalVehicles;
                    return Math.abs(vehicle.getVisitedWarehouses().size() - averageWarehouses);
                });
    }

    private Constraint limitMaxWarehouses(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .filter(vehicle -> vehicle.getVisitedWarehouses() != null)
                .penalize("Limit max warehouses per vehicle", HardSoftScore.ONE_HARD, vehicle -> {
                    int maxWarehousesPerVehicle = (int) Math.ceil((double) vehicle.getTotalWarehouses() / vehicle.getTotalVehicles());
                    int excessWarehouses = Math.max(0, vehicle.getVisitedWarehouses().size() - maxWarehousesPerVehicle);
                    return excessWarehouses;
                });
    }

    private Constraint minimizeDistance(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisitedWarehouses().isEmpty())
                .penalize("Total distance", HardSoftScore.ONE_HARD, vehicle -> {
                    List<Warehouse> visitedWarehouses = vehicle.getVisitedWarehouses();
                    int totalDistance = 0;

                    // Odległość z garażu do pierwszego magazynu
                    totalDistance += calculateDistance(vehicle.getGarage(), visitedWarehouses.get(0));
                    // Odległości między kolejnymi magazynami
                    for (int i = 0; i < visitedWarehouses.size() - 1; i++) {
                        totalDistance += calculateDistance(visitedWarehouses.get(i), visitedWarehouses.get(i + 1));
                    }
                    // Odległość z ostatniego magazynu do garażu
                    totalDistance += calculateDistance(visitedWarehouses.get(visitedWarehouses.size() - 1), vehicle.getGarage());
                    return totalDistance;
                });
    }

    private Constraint enforceTimeWindows(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .join(Warehouse.class, Joiners.filtering((vehicle, warehouse) -> {
                    int travelTime = calculateDistance(vehicle.getGarage(), warehouse);
                    int serviceStartTime = travelTime + warehouse.getServiceTime();
                    return serviceStartTime < warehouse.getOpenTime() || serviceStartTime > warehouse.getCloseTime();
                }))
                .penalize("Time window violation", HardSoftScore.ONE_HARD);
    }

    private int calculateTravelTime(Warehouse from, Warehouse to) {
        // Przykładowa kalkulacja czasu przejazdu (np. 1 km = 1 minuta)
        return calculateDistance(from, to);
    }


    private int calculateDistance(Warehouse from, Warehouse to) {
        double distance = Math.sqrt(
                Math.pow(from.getLatitude() - to.getLatitude(), 2) +
                        Math.pow(from.getLongitude() - to.getLongitude(), 2)
        );
        return (int) distance;
    }

    private int calculateDistance(Garage from, Warehouse to) {
        double distance = Math.sqrt(
                Math.pow(from.getLatitude() - to.getLatitude(), 2) +
                        Math.pow(from.getLongitude() - to.getLongitude(), 2)
        );
        return (int) distance;
    }

    private int calculateDistance(Warehouse from, Garage to) {
        double distance = Math.sqrt(
                Math.pow(from.getLatitude() - to.getLatitude(), 2) +
                        Math.pow(from.getLongitude() - to.getLongitude(), 2)
        );
        return (int) distance;
    }


    private Constraint ensureBalancedAssignment(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .filter(vehicle -> vehicle.getVisitedWarehouses().isEmpty())
                .penalize("Unbalanced assignment", HardSoftScore.ONE_HARD);
    }

    private Constraint vehicleOverCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.forEach(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisitedWarehouses().isEmpty())
                .penalize("Vehicle over capacity", HardSoftScore.ONE_HARD,
                        vehicle -> Math.max(0, calculateLoad(vehicle) - vehicle.getCapacity()));
    }

    private Constraint enforceVehicleCapacity(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisitedWarehouses().isEmpty())
                .penalize("Exceeding vehicle capacity", HardSoftScore.ONE_HARD, vehicle -> {
                    int totalDemand = vehicle.getVisitedWarehouses().stream()
                            .mapToInt(Warehouse::getLoad)
                            .sum();
                    return Math.max(0, totalDemand - vehicle.getCapacity());
                });
    }

    private int calculateLoad(Vehicle vehicle) {
        return vehicle.getVisitedWarehouses().stream()
                .mapToInt(Warehouse::getLoad)
                .sum();
    }
}