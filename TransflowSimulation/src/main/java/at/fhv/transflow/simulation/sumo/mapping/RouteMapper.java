package at.fhv.transflow.simulation.sumo.mapping;

import at.fhv.transflow.simulation.sumo.data.RouteData;
import org.eclipse.sumo.libsumo.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class RouteMapper {
    /**
     * Inner enum of all the fields (properties) of a {@link RouteData} record. Each enum constant
     * contains the integer ID of the respective SUMO route property, which is paramount to know in
     * order to handle variable subscriptions via TraCI (both for subscribing and parsing).
     * @see <a href="https://sumo.dlr.de/docs/TraCI/Route_Value_Retrieval.html">SUMO route properties</a>
     */
    public enum Fields {
        EDGES(Constants.VAR_EDGES);


        public final int sumoPropertyId;

        Fields(int sumoPropertyId) {
            this.sumoPropertyId = sumoPropertyId;
        }

        /**
         * Get a set of all the sumo property IDs in the {@link Fields} enum.
         * These can be used to batch-subscribe to a ton of specific route properties via TraCI.
         * @return A set of the integer ID of every SUMO route property of interest.
         */
        public static Set<Integer> sumoProperties() {
            return Arrays.stream(values())
                .map(field -> field.sumoPropertyId)
                .collect(Collectors.toSet());
        }
    }


    public static RouteData createRouteData(String routeId, Map<Integer, String> props) {
        if (routeId == null || props == null) return null;

        String[] edgeIds = SumoMapper.parseTraCIList(props.get(Fields.EDGES.sumoPropertyId));
        return new RouteData(routeId, edgeIds);
    }
}