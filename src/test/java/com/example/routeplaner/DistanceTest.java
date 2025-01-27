package com.example.routeplaner;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.optimization.model.RoutePlan;
import com.example.routeplaner.optimization.rules.RoutePlanConstraintProvider;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class DistanceTest {

    @Test
    public void DistanceTest() {

        String[] namesofhundred = {"c101.txt", "c105.txt", "c109.txt", "r101.txt", "r105.txt", "r109.txt", "rc101.txt", "rc105.txt", "rc108.txt"};
        String[] namesoftwohundred = {"C1_2_1.TXT", "C1_2_2.TXT", "C1_2_3.TXT", "R1_2_1.TXT", "R1_2_2.TXT", "R1_2_3.TXT", "RC1_2_1.TXT", "RC1_2_2.TXT", "RC1_2_3.TXT"};


        for (String name: namesofhundred){
            String filePath = "src/main/instances/100/" + name;
            String outputFilePath = "src/main/instances/results100.txt";
            calculateDistanceAndSave(filePath, outputFilePath, 200);
        }

        for (String name: namesoftwohundred){
            String filePath = "src/main/instances/200/" + name;
            String outputFilePath = "src/main/instances/results200.txt";
            calculateDistanceAndSave(filePath, outputFilePath, 400);
        }

    }

    public void calculateDistanceAndSave(String filePath, String outputFilePath, int timeLimit) {
        // Setting up warehouses, garages, and vehicles
        RoutePlan routePlan = new RoutePlan();
        List<Warehouse> warehouseList = new ArrayList<>();
        List<Vehicle> vehicleList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isCustomerSection = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }

                // Parse VEHICLES section
                if (line.equalsIgnoreCase("VEHICLE")) {
                    String vehicleLine1 = br.readLine();
                    String vehicleLine2 = br.readLine();

                    if (vehicleLine1 != null || vehicleLine2 != null) {
                        vehicleLine2 = vehicleLine2.trim();
                        String[] parts = vehicleLine2.split("\\s+");
                        int vehicleCount = Integer.parseInt(parts[0]);
                        int vehicleCapacity = Integer.parseInt(parts[1]);

                        for (int i = 0; i < vehicleCount; i++) {
                            Vehicle vehicle = new Vehicle();
                            vehicle.setId((long) i);
                            vehicle.setCapacity(vehicleCapacity);
                            vehicle.setName("Vehicle" + i);
                            vehicleList.add(vehicle);
                        }
                    }
                    continue;
                }

                // Parse CUSTOMERS section
                if (line.equalsIgnoreCase("CUSTOMER")) {
                    isCustomerSection = true;
                    br.readLine(); // Skip header
                    continue;
                }

                if (isCustomerSection) {
                    String[] parts = line.split("\\s+"); // Split line into parts
                    if (parts.length >= 3) {
                        String customerNo = parts[0];
                        String xCoord = parts[1];
                        String yCoord = parts[2];
                        String demand = parts[3];
                        String readyTime = parts[4];
                        String dueDate = parts[5];
                        String serviceTime = parts[6];

                        // Setting up warehouses and garage
                        if (customerNo.equals("0")) {
                            Garage garage = new Garage();
                            garage.setId(1L);
                            garage.setLatitude(Double.parseDouble(xCoord));
                            garage.setLongitude(Double.parseDouble(yCoord));
                            garage.setName("Garage");
                            routePlan.setGarageList(List.of(garage));
                        } else {
                            String warehouseName = "Warehouse" + customerNo;
                            Warehouse warehouse = new Warehouse();
                            warehouse.setId(Long.parseLong(customerNo));
                            warehouse.setLatitude(Double.parseDouble(xCoord));
                            warehouse.setLongitude(Double.parseDouble(yCoord));
                            warehouse.setName(warehouseName);
                            warehouse.setLoad(Integer.parseInt(demand));
                            warehouse.setOpenTime(Integer.parseInt(readyTime));
                            warehouse.setCloseTime(Integer.parseInt(dueDate));
                            warehouse.setServiceTime(Integer.parseInt(serviceTime));
                            warehouseList.add(warehouse);
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        routePlan.setWarehouseList(warehouseList);

        // Setting up vehicles
        for (Vehicle vehicle : vehicleList) {
            vehicle.setGarage(routePlan.getGarageList().get(0));
            vehicle.setTotalVehicles(vehicleList.size());
            vehicle.setTotalWarehouses(routePlan.getWarehouseList().size());
        }
        routePlan.setVehicleList(vehicleList);
        routePlan.getGarageList().get(0).setVehicles(vehicleList);

        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(RoutePlan.class)
                .withEntityClasses(Vehicle.class) // Klasa encji
                .withConstraintProviderClass(RoutePlanConstraintProvider.class) // Reguły punktacji
                .withTerminationSpentLimit(Duration.ofSeconds(timeLimit));

        // Configure and solve the route plan
        SolverFactory<RoutePlan> solverFactory = SolverFactory.create(solverConfig);
        Solver<RoutePlan> solver = solverFactory.buildSolver();

        RoutePlan solvedRoutePlan = solver.solve(routePlan);

        double distance = calculateTotalDistanceForSolvedRoutePlan(solvedRoutePlan);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath, true))) {
            writer.write("File: " + filePath + ", Total Distance: " + distance + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static double calculateTotalDistanceForSolvedRoutePlan(RoutePlan solvedRoutePlan) {
        double totalDistance = 0.0;

        for (Vehicle vehicle : solvedRoutePlan.getVehicleList()) {
            List<Warehouse> route = vehicle.getVisitedWarehouses();

            if (!route.isEmpty()) {
                Garage garage = vehicle.getGarage();
                Warehouse lastWarehouse = route.get(route.size() - 1);

                // Dodaj dystans z garażu do pierwszego magazynu
                totalDistance += calculateDistance(garage, route.get(0));

                // Licz dystans między kolejnymi magazynami
                for (int i = 0; i < route.size() - 1; i++) {
                    Warehouse current = route.get(i);
                    Warehouse next = route.get(i + 1);
                    totalDistance += calculateDistance(current, next);
                }

                // Dodaj dystans z ostatniego magazynu do garażu
                totalDistance += calculateDistance(lastWarehouse, garage);
            }
        }

        return totalDistance;
    }

    private static double calculateDistance(Warehouse from, Warehouse to) {
        double xDiff = from.getLatitude() - to.getLatitude();
        double yDiff = from.getLongitude() - to.getLongitude();
        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    private static double calculateDistance(Warehouse from, Garage to) {
        return Math.sqrt(
                Math.pow(from.getLatitude() - to.getLatitude(), 2) +
                        Math.pow(from.getLongitude() - to.getLongitude(), 2)
        );
    }
    private static double calculateDistance(Garage from, Warehouse to) {
        return Math.sqrt(
                Math.pow(from.getLatitude() - to.getLatitude(), 2) +
                        Math.pow(from.getLongitude() - to.getLongitude(), 2)
        );
    }

    public static void logRoutePlan(RoutePlan routePlan) {
        for (Vehicle vehicle : routePlan.getVehicleList()) {
            System.out.println("Vehicle: " + vehicle.getName());
            List<Warehouse> route = vehicle.getVisitedWarehouses();
            if (route.isEmpty()) {
                System.out.println("  No warehouses visited.");
                continue;
            }

            for (Warehouse warehouse : route) {
                System.out.println("  -> Warehouse: " + warehouse.getName());
            }
        }
    }

}