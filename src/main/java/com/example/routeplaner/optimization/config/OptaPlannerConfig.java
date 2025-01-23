package com.example.routeplaner.optimization.config;

import com.example.routeplaner.optimization.model.RoutePlan;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OptaPlannerConfig {
    @Bean
    public SolverManager<RoutePlan, Long> solverManager() {
        // Tworzenie SolverManager na podstawie SolverConfig
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("resources/routePlanSolverConfig.xml");
        return SolverManager.create(solverConfig);
    }
}
