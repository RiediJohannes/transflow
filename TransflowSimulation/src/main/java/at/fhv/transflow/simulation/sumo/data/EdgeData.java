package at.fhv.transflow.simulation.sumo.data;

import java.util.Objects;


/**
 * DTO for all data points of interest for a single road network edge at a given simulation step.
 */
public record EdgeData(
    // tags
    String id,
    Integer laneCount,
    String streetName,
    // fields
    Double sumCo2MgPerSecond,
    Double sumCoMgPerSecond,
    Double sumHcMgPerSecond,
    Double sumPmxMgPerSecond,
    Double sumNoxMgPerSecond,
    Double sumFuelConsumption,
    Double sumElectricityConsumption,
    Double sumNoiseDba,
    Integer vehicleCount,
    Double meanVehicleSpeed,
    Double meanVehicleLength,
    String[] vehicleIds,
    String[] personIds,
    Double timeOccupancyPercentage,
    Double currentTravelDuration,
    Integer haltingVehiclesCount,
    Double sumWaitingTime
) {
    public EdgeData {
        Objects.requireNonNull(id);
    }
}