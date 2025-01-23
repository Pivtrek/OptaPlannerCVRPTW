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
                balanceVehicleDistances(constraintFactory),
                enforceVehicleCapacity(constraintFactory)
        };
    }

    private Constraint balanceWorkload(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .penalize("Balance workload", HardSoftScore.ONE_SOFT, vehicle -> {
                    int totalVehicles = vehicle.getTotalVehicles();
                    int totalWarehouses = vehicle.getTotalWarehouses();
                    int averageWarehouses = totalWarehouses / totalVehicles;

                    // Penalizuj różnice w liczbie magazynów przypisanych do pojazdu
                    return Math.abs(vehicle.getVisitedWarehouses().size() - averageWarehouses);
                });
    }

    private Constraint limitMaxWarehouses(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .filter(vehicle -> vehicle.getVisitedWarehouses() != null) // Upewniamy się, że lista nie jest nullem
                .penalize("Limit max warehouses per vehicle", HardSoftScore.ONE_HARD, vehicle -> {
                    int maxWarehousesPerVehicle = (int) Math.ceil((double) vehicle.getTotalWarehouses() / vehicle.getTotalVehicles());
                    int excessWarehouses = Math.max(0, vehicle.getVisitedWarehouses().size() - maxWarehousesPerVehicle);
                    return excessWarehouses;
                });
    }
    // capacity, okna czasowa, pickup/delivery,

    private Constraint minimizeDistance(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .filter(vehicle -> !vehicle.getVisitedWarehouses().isEmpty())
                .penalize("Total distance", HardSoftScore.ONE_SOFT, vehicle -> {
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
                })
                ;
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

    private Constraint balanceVehicleDistances(ConstraintFactory constraintFactory) {
        return constraintFactory.from(Vehicle.class)
                .join(Vehicle.class,
                        Joiners.equal(Vehicle::getGarage),
                        Joiners.equal(Vehicle::getGarage))
                .penalize("Penalize unbalanced distances", HardSoftScore.ONE_SOFT,(vehicleA, vehicleB) -> {
                    int distanceA = calculateTotalDistance(vehicleA);
                    int distanceB = calculateTotalDistance(vehicleB);
                    return Math.abs(distanceA - distanceB);
                });
    }

    private int calculateTotalDistance(Vehicle vehicle) {
        List<Warehouse> visitedWarehouses = vehicle.getVisitedWarehouses();
        int totalDistance = 0;

        if (!visitedWarehouses.isEmpty()) {
            totalDistance += calculateDistance(vehicle.getGarage(), visitedWarehouses.get(0));
            for (int i = 0; i < visitedWarehouses.size() - 1; i++) {
                totalDistance += calculateDistance(visitedWarehouses.get(i), visitedWarehouses.get(i + 1));
            }
            totalDistance += calculateDistance(visitedWarehouses.get(visitedWarehouses.size() - 1), vehicle.getGarage());
        }
        return totalDistance;
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