package com.example.routeplaner.optimization.service;

import com.example.routeplaner.optimization.model.RoutePlan;
import com.example.routeplaner.optimization.dto.RoutePlanRequest;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.optimization.rules.RoutePlanConstraintProvider;
import com.example.routeplaner.repository.VehicleRepository;
import com.example.routeplaner.repository.WarehouseRepository;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.List;

@Service
public class RoutePlanService {

    private final SolverManager<RoutePlan, Long> solverManager;
    private final VehicleRepository vehicleRepository;
    private final WarehouseRepository warehouseRepository;

    public RoutePlanService(SolverManager<RoutePlan, Long> solverManager,
                            VehicleRepository vehicleRepository,
                            WarehouseRepository warehouseRepository) {
        this.solverManager = solverManager;
        this.vehicleRepository = vehicleRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public RoutePlan planRoute(RoutePlanRequest request) {
        List<Vehicle> vehicles = vehicleRepository.findAllById(request.getVehicles());
        List<Warehouse> warehouses = request.getWarehouses().stream().map(input -> {
            Warehouse warehouse = warehouseRepository.findById(input.getId())
                    .orElseThrow(() -> new RuntimeException("Warehouse not found: " + input.getId()));
            warehouse.setLoad(input.getLoad());
            warehouse.setServiceTime(input.getServiceTime());
            return warehouse;
        }).toList();

        int vehiclesNums = vehicles.size();
        int warehousesNums = warehouses.size();

        for (Vehicle vehicle : vehicles){
            System.out.println(vehicle.getName());
            vehicle.setTotalVehicles(vehiclesNums);
            vehicle.setTotalWarehouses(warehousesNums);
        }

        for(Warehouse warehouse : warehouses){
            System.out.println(warehouse.getName() + " " + warehouse.getLoad() + " " + warehouse.getServiceTime() + " " + warehouse.getOpenTime() + " " + warehouse.getCloseTime());
        }


        RoutePlan routePlan = new RoutePlan();
        routePlan.setVehicleList(vehicles);
        routePlan.setWarehouseList(warehouses);

        int timeLimitSeconds = 10;

        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(RoutePlan.class)
                .withEntityClasses(Vehicle.class) // Klasa encji
                .withConstraintProviderClass(RoutePlanConstraintProvider.class) // Reguły punktacji
                .withTerminationSpentLimit(Duration.ofSeconds(timeLimitSeconds));

        SolverFactory<RoutePlan> solverFactory = SolverFactory.create(solverConfig);
        Solver<RoutePlan> solver = solverFactory.buildSolver();

        return solver.solve(routePlan);
    }
}
