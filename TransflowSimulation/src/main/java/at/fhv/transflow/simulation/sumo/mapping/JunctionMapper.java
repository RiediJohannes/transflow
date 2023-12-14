package at.fhv.transflow.simulation.sumo.mapping;

import at.fhv.transflow.simulation.sumo.data.JunctionData;
import org.eclipse.sumo.libsumo.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class JunctionMapper {
    /**
     * Inner enum of all the fields (properties) of a {@link JunctionData} record. Each enum constant
     * contains the integer ID of the respective SUMO junction property, which is paramount to know in
     * order to handle variable subscriptions via TraCI (both for subscribing and parsing).
     * @see <a href="https://sumo.dlr.de/docs/TraCI/Junction_Value_Retrieval.html">SUMO junction properties</a>
     */
    public enum Fields {
        POSITION(Constants.VAR_POSITION),
        SHAPE(Constants.VAR_SHAPE);


        public final int sumoPropertyId;

        Fields(int sumoPropertyId) {
            this.sumoPropertyId = sumoPropertyId;
        }

        /**
         * Get a set of all the sumo property IDs in the {@link Fields} enum.
         * These can be used to batch-subscribe to a ton of specific junction properties via TraCI.
         * @return A set of the integer ID of every SUMO junction property of interest.
         */
        public static Set<Integer> sumoProperties() {
            return Arrays.stream(values())
                .map(field -> field.sumoPropertyId)
                .collect(Collectors.toSet());
        }
    }


    public static JunctionData createJunctionData(String junctionId, Map<Integer, String> props) {
        if (junctionId == null || props == null) return null;

        return new JunctionData(junctionId,
            SumoMapper.parsePosition(props.get(Fields.POSITION.sumoPropertyId)),
            SumoMapper.parseShapeList(props.get(Fields.SHAPE.sumoPropertyId))
        );
    }
}