package at.fhv.transflow.simulation.sumo.data;

import java.util.List;


public record Position(
    Double x,
    Double y,
    Double z
) {
    public Position() {
        this(null, null, null);
    }

    public Position(List<Double> coordinates) {
        // prevent OutOfBoundsExceptions, fill with null instead
        this(
            0 < coordinates.size() ? coordinates.get(0) : null,
            1 < coordinates.size() ? coordinates.get(1) : null,
            2 < coordinates.size() ? coordinates.get(2) : null
        );
    }
}