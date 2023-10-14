package at.fhv.transflow.simulation;

import at.fhv.transflow.simulation.sumo.data.VehicleData;
import at.fhv.transflow.simulation.sumo.SumoController;
import at.fhv.transflow.simulation.sumo.SumoStep;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimLoop {

    public static void main(String[] args) {
        // load the application configuration from the local properties file
        try (InputStream props = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
            AppConfig.init(props);
        } catch (IOException exp) {
            throw new RuntimeException("Failed to initialize application config!", exp);
        }

        // load the sumo config file and start a new sumo simulation from it
        Path simConfig = Paths.get(AppConfig.getProperty("sim.path").orElseThrow(() ->
            new RuntimeException("Missing property 'sim.path' in application properties! Define a path to the sumocfg file.")
        ));
        SumoController simulation = new SumoController(simConfig);


        boolean loop = true;
        for (SumoStep step : simulation) {
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