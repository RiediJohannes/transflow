package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.data.VehicleData;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SimLoop {

    public static void main(String[] args) {
        Path simConfig = Paths.get("sim/test.sumocfg");
        SumoController simulation = new SumoController(simConfig);

        boolean loop = true;
        for (SimulationStep step : simulation) {
            System.out.println(step.getId());
            System.out.println(step.getVehicleCount());

            VehicleData[] vehicleData = step.getVehicleData();

            if (vehicleData.length > 0) {
                System.out.println(vehicleData[0].speed());
            }

            if (loop && step.getId() == 20) {
                simulation.skipToStep(40);
                loop = false;
            }

            System.out.println();
        }
    }
}