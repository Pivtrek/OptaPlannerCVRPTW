package com.example.routeplaner.optimization.service;

import com.example.routeplaner.optimization.model.RoutePlan;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.stereotype.Service;

@Service
public class RouteOptimizationService {

    private final SolverManager<RoutePlan, Long> solverManager;

    public RouteOptimizationService(SolverManager<RoutePlan, Long> solverManager) {
        this.solverManager = solverManager;
    }

    public RoutePlan solve(RoutePlan routePlan) {
        return solverManager.solveAndListen(1L, id -> routePlan, bestSolution -> {
            // Handle updated solutions here (e.g., persist or notify frontend)
        });
    }
}