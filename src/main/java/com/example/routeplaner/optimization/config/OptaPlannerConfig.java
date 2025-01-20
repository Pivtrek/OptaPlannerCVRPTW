package com.example.routeplaner.optimization.config;

import com.example.routeplaner.optimization.model.RoutePlan;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.springframework.context.annotation.Bean;

public class OptaPlannerConfig {
    @Bean
    public SolverManager<RoutePlan, Long> solverManager() {
        return SolverFactory.createFromXmlResource("routePlanSolverConfig.xml").buildSolverManager();
    }
}
