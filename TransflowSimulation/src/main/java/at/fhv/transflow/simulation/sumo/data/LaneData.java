package at.fhv.transflow.simulation.sumo.data;

import java.util.List;
import java.util.Objects;


/**
 * DTO for all data points of interest for a single lane withing an edge of the road network at a given simulation step.
 */
public record LaneData(
    String id,
    String edgeId,
    String[] allowedVehicleTypes,
    String[] disallowedVehicleTypes,
    Double length,
    Double width,
    List<Double[]> shape,
    Double maxSpeed,
    Double sumCo2MgPerSecond,
    Double sumCoMgPerSecond,
    Double sumHcMgPerSecond,
    Double sumPmxMgPerSecond,
    Double sumNoxMgPerSecond,
    Double sumFuelConsumption,
    Double sumElectricityConsumption,
    Double sumNoiseDba,
    Integer vehicleCount,
    String[] vehicleIds,
    Double timeOccupancyPercentage,
    Double meanVehicleSpeed,
    Double meanVehicleLength,
    Double sumWaitingTime,
    Double currentTravelDuration,
    Integer haltingVehiclesCount,
    Double startToEndAngle,
    String[] laneChangeAllowedLeft,
    String[] laneChangeAllowedRight,
    List<Link> links
) {
    public LaneData {
        Objects.requireNonNull(id);
    }

    public record Link(
        String approachedLane,
        Boolean hasPrio,
        Boolean isOpen,
        Boolean hasFoe,
        String approachedInternal,
        String state,
        String direction,
        Double length
    ) {}
}