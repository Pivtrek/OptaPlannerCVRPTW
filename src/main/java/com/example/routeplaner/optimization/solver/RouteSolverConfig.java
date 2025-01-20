package com.example.routeplaner.optimization.solver;

import com.example.routeplaner.optimization.model.Customer;
import com.example.routeplaner.optimization.model.RoutePlan;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

public class RouteSolverConfig {

    public SolverFactory<?> solverFactory() {
        return SolverFactory.create(new SolverConfig()
                .withSolutionClass(RoutePlan.class)
                .withEntityClasses(Customer.class)
                .withConstraintProviderClass(RouteConstraintProvider.class));
    }
}