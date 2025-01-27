package com.example.routeplaner.optimization.controller;

import com.example.routeplaner.optimization.model.RoutePlan;
import com.example.routeplaner.optimization.service.RoutePlanService;
import com.example.routeplaner.optimization.dto.RoutePlanRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/routePlan")
public class RoutePlanController {

    private final RoutePlanService routePlanService;

    public RoutePlanController(RoutePlanService routePlanService) {
        this.routePlanService = routePlanService;
    }

    @PostMapping
    public ResponseEntity<?> createRoutePlan(@RequestBody RoutePlanRequest request) {
        RoutePlan solvedPlan = routePlanService.planRoute(request);

        // Mapowanie tras na format dla Google Maps
        List<Map<String, Object>> routes = solvedPlan.getVehicleList().stream().map(vehicle -> {
            Map<String, Object> route = new HashMap<>();
            route.put("vehicle", vehicle.getName());
            List<Map<String, Double>> path = vehicle.getVisitedWarehouses().stream()
                    .map(warehouse -> Map.of("lat", warehouse.getLatitude(), "lng", warehouse.getLongitude()))
                    .collect(Collectors.toList());

            // Dodanie punktu startowego (garaż) i końcowego
            path.add(0, Map.of("lat", vehicle.getGarage().getLatitude(), "lng", vehicle.getGarage().getLongitude()));
            path.add(Map.of("lat", vehicle.getGarage().getLatitude(), "lng", vehicle.getGarage().getLongitude()));

            route.put("path", path);
            return route;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("routes", routes));
    }
}