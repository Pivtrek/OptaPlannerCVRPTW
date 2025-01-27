package com.example.routeplaner.optimization.config;

import com.example.routeplaner.model.Vehicle;
import com.example.routeplaner.model.Warehouse;
import com.example.routeplaner.optimization.model.RoutePlan;
import com.example.routeplaner.optimization.rules.RoutePlanConstraintProvider;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OptaPlannerConfig {
    @Bean
    public SolverManager<RoutePlan, Long> solverManager() {
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(RoutePlan.class)
                .withEntityClasses(Vehicle.class)
                .withConstraintProviderClass(RoutePlanConstraintProvider.class)
                .withTerminationSpentLimit(Duration.ofMinutes(1)); // Limit czasu działania solvera

        return SolverManager.create(solverConfig);
    }
}
