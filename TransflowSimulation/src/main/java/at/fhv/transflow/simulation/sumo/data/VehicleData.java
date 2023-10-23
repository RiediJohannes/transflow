package at.fhv.transflow.simulation.sumo.data;

import java.util.List;
import java.util.Objects;

public record VehicleData(
    // tags
    String id,
    String hexColor,
    String vehicleTypeId,
    double length,
    double width,
    double height,
    int personCapacity,
    double maxSpeed,
    double maxAcceleration,
    double maxDeceleration,
    double speedFactor,
    double speedDeviation,
    String shapeClass,
    double tau,
    double sigma,
    String routeId,
    double minFrontGap,
    double minLateralGap,

    // fields
    double speed,
    double acceleration,
    double lateralSpeed,
    double allowedSpeed,
    double angle,
    String roadId,
    int edgeInRoute,
    int lane,
    double PositionOnLane,
    int laneChangeState,
    int signalState,
    int stopState,
    List<String> personIds,
    double CO2mgPerSecond,
    double HCmgPerSecond,
    double PMXmgPerSecond,
    double NOXmgPerSecond,
    double fuelConsumption,
    double electricityConsumption,
    double noiseDBA,
    double totalDistance,
    double totalWaitingTime,
    double totalTimeLoss,
    double boardingDuration,
    String leaderVehicleId,
    double leaderVehicleDistance
) {
    public VehicleData {
        Objects.requireNonNull(id);
    }
}