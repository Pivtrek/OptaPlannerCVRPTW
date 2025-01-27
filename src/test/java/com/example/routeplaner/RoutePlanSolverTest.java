package com.example.routeplaner;
import com.example.routeplaner.model.Garage;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.optimization.model.RoutePlan;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoutePlanSolverTest {
    @Test
    public void testRoutePlanSolver() {
        // Wczytaj konfigurację solvera
        SolverFactory<RoutePlan> solverFactory = SolverFactory.createFromXmlFile(new File("src/main/resources/routePlanSolverConfig.xml"));

        // Stwórz solver
        Solver<RoutePlan> solver = solverFactory.buildSolver();

        // Stwórz przykładowy RoutePlan
        RoutePlan routePlan = createExampleRoutePlan();


//        for (Vehicle vehicle : routePlan.getVehicleList()) {
//            vehicle.setVisitedWarehouses(new ArrayList<>());
//            vehicle.getVisitedWarehouses().add(routePlan.getWarehouseList().getFirst()); // Przypisz magazyn startowy tutaj
//        }

        System.out.println("Initial RoutePlan:");
        logRoutePlan(routePlan);
        // Rozwiąż problem
        RoutePlan solvedRoutePlan = solver.solve(routePlan);

        // Sprawdź, czy rozwiązanie zostało wygenerowane

        // Check if solution is generated
        assertNotNull(solvedRoutePlan);


        // Log solved state
        System.out.println("Solved RoutePlan:");
        logRoutePlan(solvedRoutePlan);

    }
    public RoutePlan createExampleRoutePlan() {

        RoutePlan routePlan = new RoutePlan();

        Warehouse warehouse1 = new Warehouse();
        Warehouse warehouse2 = new Warehouse();
        Warehouse warehouse3 = new Warehouse();
        Warehouse warehouse4 = new Warehouse();
        Warehouse warehouse5 = new Warehouse();
        Warehouse warehouse6 = new Warehouse();

        warehouse1.setId(1L);
        warehouse2.setId(2L);
        warehouse3.setId(3L);
        warehouse4.setId(4L);
        warehouse5.setId(5L);
        warehouse6.setId(6L);

        warehouse1.setName("Warehouse1");
        warehouse2.setName("Warehouse2");
        warehouse3.setName("Warehouse3");
        warehouse4.setName("Warehouse4");
        warehouse5.setName("Warehouse5");
        warehouse6.setName("Warehouse6");

        warehouse1.setLatitude(52.2296756); // Warszawa
        warehouse1.setLongitude(21.0122287);

        warehouse2.setLatitude(50.0619474); // Kraków
        warehouse2.setLongitude(19.9368564);

        warehouse3.setLatitude(51.1078852); // Wrocław
        warehouse3.setLongitude(17.0385376);

        warehouse4.setLatitude(20.3520252); // Gdańsk
        warehouse4.setLongitude(50.6466384);

        warehouse5.setLatitude(30.8225303); // Katowice
        warehouse5.setLongitude(-5.0835126);

        warehouse6.setLatitude(53.4285438); // Szczecin
        warehouse6.setLongitude(-10.5528118);

        warehouse1.setLoad(2000);
        warehouse2.setLoad(1000);
        warehouse3.setLoad(1000);
        warehouse4.setLoad(2000);
        warehouse5.setLoad(500);
        warehouse6.setLoad(1000);

        routePlan.setWarehouseList(List.of(warehouse1, warehouse2, warehouse3, warehouse4, warehouse5, warehouse6));

        Garage garage1 = new Garage();
        garage1.setId(1L);
        garage1.setLongitude(23.0122287);
        garage1.setLatitude(70.2296756);
        garage1.setName("Garage1");

        Garage garage2 = new Garage();
        garage2.setId(2L);
        garage2.setLongitude(20.0);
        garage2.setLatitude(20.0);
        garage2.setName("Garage2");


        Vehicle vehicle1 = new Vehicle();
        Vehicle vehicle2 = new Vehicle();

        vehicle1.setId(1L);
        vehicle2.setId(2L);

        vehicle1.setName("Vehicle1");
        vehicle2.setName("Vehicle2");

        vehicle1.setCapacity(2000);
        vehicle2.setCapacity(3000);
        vehicle1.setGarage(garage1);
        vehicle2.setGarage(garage2);
        vehicle1.setTotalVehicles(2);
        vehicle2.setTotalVehicles(2);
        vehicle1.setTotalWarehouses(routePlan.getWarehouseList().size());
        vehicle2.setTotalWarehouses(routePlan.getWarehouseList().size());

        routePlan.setVehicleList(List.of(vehicle1, vehicle2));

        garage1.setVehicles(List.of(vehicle1));
        garage2.setVehicles(List.of(vehicle2));

        routePlan.setGarageList(List.of(garage1, garage2));

        return routePlan;
    }

    private void logRoutePlan(RoutePlan routePlan) {
        for (Vehicle vehicle : routePlan.getVehicleList()) {
            System.out.println("Vehicle: " + vehicle.getName());

            List<Warehouse> visitedWarehouses = vehicle.getVisitedWarehouses();
            if (visitedWarehouses == null || visitedWarehouses.isEmpty()) {
                System.out.println(" - No warehouses assigned.");
                continue;
            }

            int tripNumber = 1;
            int currentLoad = 0;
            System.out.println(" Trips:");
            for (Warehouse warehouse : visitedWarehouses) {
                // Logika rozdzielania na kursy
                if (currentLoad + warehouse.getLoad() > vehicle.getCapacity()) {
                    System.out.println("  Trip " + tripNumber + " ends at capacity.");
                    tripNumber++;
                    currentLoad = 0;
                }
                System.out.println("   Trip " + tripNumber + " - Visits: " + warehouse.getName() + " (Load: " + warehouse.getLoad() + ")");
                currentLoad += warehouse.getLoad();
            }
            System.out.println("  Trip " + tripNumber + " ends at garage.");
        }
    }
}
