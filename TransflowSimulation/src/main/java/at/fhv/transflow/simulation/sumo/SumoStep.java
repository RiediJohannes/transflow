package at.fhv.transflow.simulation.sumo;

import at.fhv.transflow.simulation.sumo.data.SumoMapper;
import at.fhv.transflow.simulation.sumo.data.VehicleData;
import org.eclipse.sumo.libsumo.Vehicle;

public class SumoStep {
    private final int id;

    public SumoStep(int stepId) {
        this.id = stepId;
    }


    public int getId() {
        return id;
    }

    public int getVehicleCount() {
        return Vehicle.getIDCount();
    }

    public VehicleData[] getVehicleData() {
        // create a VehicleData object from every vehicle ID that's currently in the simulation
        return Vehicle.getIDList().stream()
            .map(id -> new VehicleData(
                id,
                SumoMapper.colorFromTraCI(Vehicle.getColor(id)),
                Vehicle.getVehicleClass(id),
                Vehicle.getLength(id),
                Vehicle.getWidth(id),
                Vehicle.getHeight(id),
                Vehicle.getPersonCapacity(id),
                Vehicle.getMaxSpeed(id),
                Vehicle.getAcceleration(id),
                Vehicle.getDecel(id),
                Vehicle.getSpeedFactor(id),
                Vehicle.getSpeedDeviation(id),
                Vehicle.getShapeClass(id),
                Vehicle.getTau(id),
                Vehicle.getImperfection(id),
                Vehicle.getRouteID(id),
                Vehicle.getMinGap(id),
                Vehicle.getMinGapLat(id),

                Vehicle.getSpeed(id),
                Vehicle.getAcceleration(id),
                Vehicle.getLateralSpeed(id),
                Vehicle.getAllowedSpeed(id),
                Vehicle.getAngle(id),
                Vehicle.getRoadID(id),
                Vehicle.getRouteIndex(id),
                Vehicle.getLaneIndex(id),
                Vehicle.getLanePosition(id),
                Vehicle.getLaneChangeMode(id),
                Vehicle.getSignals(id),
                Vehicle.getStopState(id),
                Vehicle.getPersonIDList(id),
                Vehicle.getCO2Emission(id),
                Vehicle.getHCEmission(id),
                Vehicle.getPMxEmission(id),
                Vehicle.getNOxEmission(id),
                Vehicle.getFuelConsumption(id),
                Vehicle.getElectricityConsumption(id),
                Vehicle.getNoiseEmission(id),
                Vehicle.getDistance(id),
                Vehicle.getWaitingTime(id),
                Vehicle.getTimeLoss(id),
                Vehicle.getBoardingDuration(id),
                Vehicle.getLeader(id).getFirst(),
                Vehicle.getLeader(id).getSecond()
            ))
            .toArray(VehicleData[]::new);
    }
}