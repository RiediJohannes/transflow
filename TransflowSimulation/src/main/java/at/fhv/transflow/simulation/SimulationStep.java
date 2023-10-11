package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.data.VehicleData;

public interface SimulationStep {
    int getId();

    int getVehicleCount();

    VehicleData[] getVehicleData();
}