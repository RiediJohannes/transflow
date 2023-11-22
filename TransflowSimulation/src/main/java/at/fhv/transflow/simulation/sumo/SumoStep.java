package at.fhv.transflow.simulation.sumo;

import at.fhv.transflow.simulation.sumo.data.VehicleData;
import at.fhv.transflow.simulation.sumo.mapping.VehicleMapper;
import org.eclipse.sumo.libsumo.SubscriptionResults;
import org.eclipse.sumo.libsumo.Vehicle;

import java.util.Map;
import java.util.stream.Collectors;

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
        SubscriptionResults results = Vehicle.getAllSubscriptionResults();

        return results.entrySet().stream().parallel()
            .map(entry -> {
                // create a new map without the nasty TraCIResult type as value
                Map<Integer, String> properties = entry.getValue().entrySet().stream().parallel()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        property -> property.getValue().getString() // get string value of TraCIResult
                    ));

                // create a VehicleData record from every subscription result
                return VehicleMapper.createVehicleData(entry.getKey(), properties);
            })
            .toArray(VehicleData[]::new);
    }
}