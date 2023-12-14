package at.fhv.transflow.simulation.sumo.data;

import java.util.List;
import java.util.Objects;


/**
 * DTO for all data points of interest for a single road junction at a given simulation step.
 */
public record JunctionData(
    // tags
    String id,
    Position position,
    List<Double[]> shape
) {
    public JunctionData {
        Objects.requireNonNull(id);
    }
}