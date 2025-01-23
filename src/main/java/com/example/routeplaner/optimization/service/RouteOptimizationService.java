package com.example.routeplaner.optimization.service;

import com.example.routeplaner.model.Garage;
import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.optimization.model.RoutePlan;
import org.hibernate.sql.exec.ExecutionException;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteOptimizationService {

    @Autowired
    private final SolverManager<RoutePlan, Long> solverManager;

    public RouteOptimizationService(SolverManager<RoutePlan, Long> solverManager) {
        this.solverManager = solverManager;
    }

    public RoutePlan solve(RoutePlan routePlan) {
        SolverJob<RoutePlan, Long> solverJob = solverManager.solveAndListen(
                1L, // Unikalny identyfikator dla sesji rozwiązywania
                id -> routePlan, // Funkcja do załadowania początkowego problemu
                bestSolution -> {
                    // Obsługa najlepszego rozwiązania (np. zapis do bazy danych lub powiadomienie frontend)
                    System.out.println("Zaktualizowane rozwiązanie: " + bestSolution);
                }
        );

        // Opcjonalnie: sprawdzenie statusu lub czekanie na zakończenie pracy solvera
        try {
            RoutePlan finalSolution = solverJob.getFinalBestSolution();
            System.out.println("Ostateczne rozwiązanie: " + finalSolution);
        } catch (InterruptedException | ExecutionException | java.util.concurrent.ExecutionException e) {
            System.err.println("Błąd podczas rozwiązywania: " + e.getMessage());
        }
        return routePlan;
    }
}