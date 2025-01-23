package com.example.routeplaner.controller;

import com.example.routeplaner.optimization.model.RoutePlan;
import com.example.routeplaner.optimization.service.RouteOptimizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route")
public class RoutePlanController {

    @Autowired
    private RouteOptimizationService routePlanService;

    /**
     * Przyjmuje dane od frontendu i uruchamia algorytm OptaPlannera
     *
     * @param routePlan Obiekt RoutePlan przesłany z frontendu
     * @return Najlepsze rozwiązanie wygenerowane przez OptaPlannera
     */
    @PostMapping("/optimize")
    public ResponseEntity<RoutePlan> optimizeRoute(@RequestBody RoutePlan routePlan) {
        // Rozwiąż problem z OptaPlannerem
        RoutePlan solvedRoutePlan = routePlanService.solve(routePlan);

        // Zwróć wynik
        return ResponseEntity.ok(solvedRoutePlan);
    }

//    @PostMapping("/test-optaplanner")
//    public ResponseEntity<RoutePlan> testOptaPlanner() {
//        RoutePlan examplePlan = routePlanService.createExampleRoutePlan();
//        RoutePlan solvedPlan = routePlanService.solve(examplePlan);
//        return ResponseEntity.ok(solvedPlan);
//    }

    /**
     * Przykladowe endpointy dla debugowania lub uzyskania przykładowych danych
     */

    /*
    @GetMapping("/example")
    public ResponseEntity<RoutePlan> getExampleRoutePlan() {
        // Przygotuj przykładowy obiekt RoutePlan
        RoutePlan exampleRoutePlan = routePlanService.createExampleRoutePlan();
        return ResponseEntity.ok(exampleRoutePlan);
    }

     */
}
