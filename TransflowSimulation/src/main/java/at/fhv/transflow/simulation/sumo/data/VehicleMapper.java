package at.fhv.transflow.simulation.sumo.data;

import org.eclipse.sumo.libsumo.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class VehicleMapper {
    // ID of the leader property which cannot be subscribed by the normal subscribe method
    public final static int LEADER_PROPERTY = 0x68;

    /**
     * Inner enum of all the fields (properties) of a {@link VehicleData} record. Each enum constant
     * contains the integer ID of the respective SUMO vehicle property, which is paramount to know in
     * order to handle variable subscriptions via TraCI (both for subscribing and parsing).
     * @see <a href="https://sumo.dlr.de/docs/TraCI/Vehicle_Value_Retrieval.html">SUMO vehicle properties</a>
     */
    public enum Fields {
        HEX_COLOR(Constants.VAR_COLOR),
        VEHICLE_TYPE_ID(Constants.VAR_VEHICLECLASS),
        LENGTH(Constants.VAR_LENGTH),
        WIDTH(Constants.VAR_WIDTH),
        HEIGHT(Constants.VAR_HEIGHT),
        PERSON_CAPACITY(Constants.VAR_PERSON_CAPACITY),
        MAX_SPEED(Constants.VAR_MAXSPEED),
        MAX_ACCELERATION(Constants.VAR_ACCEL),
        MAX_DECELERATION(Constants.VAR_DECEL),
        SPEED_FACTOR(Constants.VAR_SPEED_FACTOR),
        SPEED_DEVIATION(Constants.VAR_SPEED_DEVIATION),
        SHAPE_CLASS(Constants.VAR_SHAPECLASS),
        TAU(Constants.VAR_TAU),
        SIGMA(Constants.VAR_IMPERFECTION),
        ROUTE_ID(Constants.VAR_ROUTE_ID),
        MIN_FRONT_GAP(Constants.VAR_MINGAP),
        MIN_LATERAL_GAP(Constants.VAR_MINGAP_LAT),
        SPEED(Constants.VAR_SPEED),
        ACCELERATION(Constants.VAR_ACCELERATION),
        LATERAL_SPEED(Constants.VAR_SPEED_LAT),
        ALLOWED_SPEED(Constants.VAR_ALLOWED_SPEED),
        ANGLE(Constants.VAR_ANGLE),
        ROAD_ID(Constants.VAR_ROAD_ID),
        EDGE_IN_ROUTE(Constants.VAR_ROUTE_INDEX),
        LANE(Constants.VAR_LANE_INDEX),
        POSITION_ON_LANE(Constants.VAR_LANEPOSITION),
        LANE_CHANGE_STATE(Constants.VAR_LANECHANGE_MODE),
        SIGNAL_STATE(Constants.VAR_SIGNALS),
        STOP_STATE(Constants.VAR_STOPSTATE),
        PERSON_IDS(Constants.LAST_STEP_PERSON_ID_LIST),
        CO_2_MG_PER_SECOND(Constants.VAR_CO2EMISSION),
        HC_MG_PER_SECOND(Constants.VAR_HCEMISSION),
        PMX_MG_PER_SECOND(Constants.VAR_PMXEMISSION),
        NOX_MG_PER_SECOND(Constants.VAR_NOXEMISSION),
        FUEL_CONSUMPTION(Constants.VAR_FUELCONSUMPTION),
        ELECTRICITY_CONSUMPTION(Constants.VAR_ELECTRICITYCONSUMPTION),
        NOISE_DBA(Constants.VAR_NOISEEMISSION),
        TOTAL_DISTANCE(Constants.VAR_DISTANCE),
        TOTAL_WAITING_TIME(Constants.VAR_WAITING_TIME),
        TOTAL_TIME_LOSS(Constants.VAR_TIMELOSS),
        BOARDING_DURATION(Constants.VAR_BOARDING_DURATION);


        public final int sumoPropertyId;

        Fields(int sumoPropertyId) {
            this.sumoPropertyId = sumoPropertyId;
        }

        /**
         * Get a set of all the sumo property IDs in the {@link Fields} enum.
         * These can be used to batch-subscribe to a ton of specific vehicles properties via TraCI.
         * @return A set of the integer ID of every SUMO vehicle property of interest.
         */
        public static Set<Integer> sumoProperties() {
            return Arrays.stream(values())
                .map(field -> field.sumoPropertyId)
                .collect(Collectors.toSet());
        }
    }

    public static VehicleData createVehicleData(String vehicleId, Map<Integer, String> props) {
        // parse the values of the leader subscription
        Leader leader = VehicleMapper.parseLeader(props.get(VehicleMapper.LEADER_PROPERTY));
        // parse the list of riding person IDs
        String[] personIds = SumoMapper.parseTraCIList(props.get(VehicleMapper.Fields.PERSON_IDS.sumoPropertyId));

        return new VehicleData(vehicleId,
            SumoMapper.hexColorFromTraCI(props.get(Fields.HEX_COLOR.sumoPropertyId)),
            props.get(Fields.VEHICLE_TYPE_ID.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.LENGTH.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.WIDTH.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.HEIGHT.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.PERSON_CAPACITY.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_ACCELERATION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_DECELERATION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SPEED_FACTOR.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SPEED_DEVIATION.sumoPropertyId)),
            props.get(Fields.SHAPE_CLASS.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.TAU.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SIGMA.sumoPropertyId)),
            props.get(Fields.ROUTE_ID.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.MIN_FRONT_GAP.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MIN_LATERAL_GAP.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.ACCELERATION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.LATERAL_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.ALLOWED_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.ANGLE.sumoPropertyId)),
            props.get(Fields.ROAD_ID.sumoPropertyId),
            SumoMapper.tryParseInteger(props.get(Fields.EDGE_IN_ROUTE.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.LANE.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.POSITION_ON_LANE.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.LANE_CHANGE_STATE.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.SIGNAL_STATE.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.STOP_STATE.sumoPropertyId)),
            personIds,
            SumoMapper.tryParseDouble(props.get(Fields.CO_2_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.HC_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.PMX_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.NOX_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.FUEL_CONSUMPTION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.ELECTRICITY_CONSUMPTION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.NOISE_DBA.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.TOTAL_DISTANCE.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.TOTAL_WAITING_TIME.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.TOTAL_TIME_LOSS.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.BOARDING_DURATION.sumoPropertyId)),
            leader.id,
            leader.distance
        );
    }

    /**
     * Parses a {@link Leader} record from the string representation of the response to a
     * TraCI subscription on a vehicle's leader.<br>
     * These come in the form of <code>"TraCIRoadPosition(&lt;leaderId&gt;,&lt;leaderDistance&gt;)"</code>.
     * @param leaderResultString The string representation of a TraCI response to the vehicle leader subscription.
     * @return An initialized leader record which either contains the ID and distance of a parsed
     * leader or null values for ID and distance if no leader properties were found within the given string.
     * This method never returns null, just a Leader with null fields.
     */
    private static Leader parseLeader(String leaderResultString) {
        if (leaderResultString == null) return Leader.NONE;

        Pattern pattern = Pattern.compile("(?<=[(]).+,.+(?=[)])");
        Matcher matcher = pattern.matcher(leaderResultString);

        if (matcher.find()) {
            String[] arguments = matcher.group().split(",");

            if (arguments.length == 2) {
                String leaderId = arguments[0];

                // if no leader was found, the value of the leader ID is prefixed with an underscore
                if (!leaderId.startsWith("_")) {
                    // we have successfully found the leader attributes
                    Double leaderDistance = SumoMapper.tryParseDouble(arguments[1]);
                    return new Leader(leaderId, leaderDistance);
                }
            }
        }

        return Leader.NONE;
    }

    /**
     * Tiny, internal data structure to hold the two values (vehicle ID and distance) from a leader
     * subscription result. This record only serves the purpose of aiding the parsing logic
     * inside the {@link #createVehicleData(String, Map) createVehicleData(id, props)} method.
     * @param id       The ID of a leading vehicle.
     * @param distance The distance of a leading vehicle to the vehicle behind it.
     */
    private record Leader(String id, Double distance) {
        public static final Leader NONE = new Leader(null, null);
    }
}