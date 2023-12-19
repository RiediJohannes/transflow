package at.fhv.transflow.simulation.sumo.data;

import java.util.List;
import java.util.Objects;


/**
 * DTO for all data points of interest for a single vehicle type at a given simulation step.
 */
public record VehicleTypeData(
    String id,
    String vehicleClass,
    Double length,
    Double width,
    Double height,
    List<Double[]> shape,
    String hexColor,
    Double maxSpeed,
    Double maxSpeedLateral,
    Double maxAcceleration,
    Double maxDeceleration,
    Double speedFactor,
    Double speedDeviation,
    String emissionClass,
    Double scale,
    Double tau,
    Double sigma,
    Double minGap,
    Double minLateralGap,
    String lateralAlignment,
    Double actionStepLength,
    Integer personCapacity,
    Double boardingDuration
) implements SumoObject {
    public VehicleTypeData {
        Objects.requireNonNull(id);
    }
}