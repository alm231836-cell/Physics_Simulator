package com.physicssim.model;

import java.util.List;

public final class SimulationCatalog {

    private SimulationCatalog() {
    }

    public static List<SimulationItem> homeItems() {
        return List.of(
                new SimulationItem("(1)", "Pendulum\nDynamics", SimulationType.PENDULUM),
                new SimulationItem("(2)", "Mechanics &\nElasticity", SimulationType.MECHANICS),
                new SimulationItem("(3)", "Orbital Gravity", SimulationType.ORBIT),
                new SimulationItem("(4)", "Data Analysis", SimulationType.ANALYTICS));
    }
}
