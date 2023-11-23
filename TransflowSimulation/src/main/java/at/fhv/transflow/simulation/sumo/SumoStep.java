package at.fhv.transflow.simulation.sumo;

import at.fhv.transflow.simulation.sumo.data.EdgeData;
import at.fhv.transflow.simulation.sumo.data.VehicleData;
import at.fhv.transflow.simulation.sumo.mapping.EdgeMapper;
import at.fhv.transflow.simulation.sumo.mapping.VehicleMapper;
import org.eclipse.sumo.libsumo.Edge;
import org.eclipse.sumo.libsumo.SubscriptionResults;
import org.eclipse.sumo.libsumo.TraCIResults;
import org.eclipse.sumo.libsumo.Vehicle;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a single time step of a running SUMO traffic simulation. Provides a simple API to query previously subscribed
 * simulation metrics such as {@link #getVehicleData()}.
 */
public class SumoStep {
    private final int id;

    public SumoStep(int stepId) {
        this.id = stepId;
    }


    public int getId() {
        return id;
    }

    /**
     * Returns the number of vehicles that are present in the current time step.
     */
    public int getVehicleCount() {
        return Vehicle.getIDCount();
    }

    public VehicleData[] getVehicleData() {
        SubscriptionResults allResults = Vehicle.getAllSubscriptionResults();

        return allResults.entrySet().stream().parallel()
            .map(resultsPerId -> {
                Map<Integer, String> properties = extractPropertyMap(resultsPerId);
                return VehicleMapper.createVehicleData(resultsPerId.getKey(), extractPropertyMap(resultsPerId));
            })
            .toArray(VehicleData[]::new);
    }

    public EdgeData[] getEdgeData() {
        SubscriptionResults allResults = Edge.getAllSubscriptionResults();

        return allResults.entrySet().stream().parallel()
            .map(resultsPerId -> {
                Map<Integer, String> properties = extractPropertyMap(resultsPerId);
                return EdgeMapper.createEdgeData(resultsPerId.getKey(), properties);
            })
            .toArray(EdgeData[]::new);
    }

    /**
     * Takes in a single {@link Map.Entry} of {@link TraCIResults} as the value (containing the subscription
     * values related to a specific entity) and the ID of the related SUMO entity as the key. This method
     * then returns a new {@link Map} of all properties included in the TraCIResults with the respective property's
     * integer identifier as the key and the String representation of the property's value as the map's value.
     * @param entry {@link Map.Entry} of subscription results for a single SUMO entity.
     * @return A {@link Map Map&lt;Integer, String&gt;} of every property found in the given {@link TraCIResults}.
     * The map's keys contain each property's integer identifier and the map's values contain the String representation
     * of the respective property's value.
     */
    private Map<Integer, String> extractPropertyMap(Map.Entry<String, TraCIResults> entry) {
        // create a new map without the nasty TraCIResult type as value
        return entry.getValue().entrySet().stream().parallel()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                property -> property.getValue().getString() // get string value of TraCIResult
            ));
    }
}