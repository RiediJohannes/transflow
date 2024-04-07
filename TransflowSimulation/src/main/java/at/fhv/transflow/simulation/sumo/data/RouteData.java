package at.fhv.transflow.simulation.sumo.data;

import java.util.Objects;


/**
 * DTO for all data points of interest for a single driving route at a given simulation step.
 */
public record RouteData(String id, String[] edges) implements SumoObject {
    public RouteData {
        Objects.requireNonNull(id);
    }
}