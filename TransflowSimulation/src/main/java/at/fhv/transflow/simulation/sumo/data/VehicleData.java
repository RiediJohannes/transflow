package at.fhv.transflow.simulation.sumo.data;

import java.util.Objects;


/**
 * DTO for all data points of interest for a single vehicle at a given simulation step.
 */
public record VehicleData(
    // tags
    String id,
    String hexColor,
    String vehicleTypeId,
    Double length,
    Double width,
    Double height,
    Integer personCapacity,
    Double maxSpeed,
    Double maxAcceleration,
    Double maxDeceleration,
    Double speedFactor,
    Double speedDeviation,
    String shapeClass,
    Double tau,
    Double sigma,
    String routeId,
    Double minFrontGap,
    Double minLateralGap,

    // fields
    Double speed,
    Double acceleration,
    Double lateralSpeed,
    Double allowedSpeed,
    Double angle,
    String roadId,
    Integer edgeInRoute,
    Integer lane,
    Position position,
    Double positionOnLane,
    Integer laneChangeState,
    Integer signalState,
    Integer stopState,
    String[] personIds,
    Double CO2mgPerSecond,
    Double HCmgPerSecond,
    Double PMXmgPerSecond,
    Double NOXmgPerSecond,
    Double fuelConsumption,
    Double electricityConsumption,
    Double noiseDBA,
    Double totalDistance,
    Double totalWaitingTime,
    Double totalTimeLoss,
    Double boardingDuration,
    String leaderVehicleId,
    Double leaderVehicleDistance
) {
    // restrictions for constructor
    public VehicleData {
        Objects.requireNonNull(id);
    }
}