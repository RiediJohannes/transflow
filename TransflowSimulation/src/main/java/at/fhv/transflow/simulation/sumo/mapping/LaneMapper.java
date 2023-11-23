package at.fhv.transflow.simulation.sumo.mapping;

import at.fhv.transflow.simulation.sumo.data.LaneData;
import org.eclipse.sumo.libsumo.Constants;
import org.eclipse.sumo.libsumo.TraCIConnection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class LaneMapper {
    /**
     * Inner enum of all the fields (properties) of a {@link LaneData} record. Each enum constant
     * contains the integer ID of the respective SUMO lane property, which is paramount to know in
     * order to handle variable subscriptions via TraCI (both for subscribing and parsing).
     * @see <a href="https://sumo.dlr.de/docs/TraCI/Lane_Value_Retrieval.html">SUMO lane properties</a>
     */
    public enum Fields {
        EDGE_ID(Constants.LANE_EDGE_ID),
        ALLOWED_VEHICLE_TYPES(Constants.LANE_ALLOWED),
        DISALLOWED_VEHICLE_TYPES(Constants.LANE_DISALLOWED),
        LENGTH(Constants.VAR_LENGTH),
        WIDTH(Constants.VAR_WIDTH),
        SHAPE(Constants.VAR_SHAPE),
        MAX_SPEED(Constants.VAR_MAXSPEED),
        SUM_CO2_MG_PER_SECOND(Constants.VAR_CO2EMISSION),
        SUM_CO_MG_PER_SECOND(Constants.VAR_COEMISSION),
        SUM_HC_MG_PER_SECOND(Constants.VAR_HCEMISSION),
        SUM_PMX_MG_PER_SECOND(Constants.VAR_PMXEMISSION),
        SUM_NOX_MG_PER_SECOND(Constants.VAR_NOXEMISSION),
        SUM_FUEL_CONSUMPTION(Constants.VAR_FUELCONSUMPTION),
        SUM_ELECTRICITY_CONSUMPTION(Constants.VAR_ELECTRICITYCONSUMPTION),
        SUM_NOISE_DBA(Constants.VAR_NOISEEMISSION),
        VEHICLE_COUNT(Constants.LAST_STEP_VEHICLE_NUMBER),
        VEHICLE_IDS(Constants.LAST_STEP_VEHICLE_ID_LIST),
        TIME_OCCUPANCY_PERCENTAGE(Constants.LAST_STEP_OCCUPANCY),
        MEAN_VEHICLE_SPEED(Constants.LAST_STEP_MEAN_SPEED),
        MEAN_VEHICLE_LENGTH(Constants.VAR_LENGTH),
        SUM_WAITING_TIME(Constants.VAR_WAITING_TIME),
        CURRENT_TRAVEL_DURATION(Constants.VAR_CURRENT_TRAVELTIME),
        HALTING_VEHICLES_COUNT(Constants.LAST_STEP_VEHICLE_HALTING_NUMBER),
        LINKS(Constants.LANE_LINKS);


        public final int sumoPropertyId;

        Fields(int sumoPropertyId) {
            this.sumoPropertyId = sumoPropertyId;
        }

        /**
         * Get a set of all the sumo property IDs in the {@link Fields} enum.
         * These can be used to batch-subscribe to a ton of specific lane properties via TraCI.
         * @return A set of the integer ID of every SUMO lane property of interest.
         */
        public static Set<Integer> sumoProperties() {
            return Arrays.stream(values())
                .map(field -> field.sumoPropertyId)
                .collect(Collectors.toSet());
        }
    }

    public enum ExtraFields {
        LANE_CHANGE_ALLOWED_LEFT(-1),
        LANE_CHANGE_ALLOWED_RIGHT(-2);

        public final int id;

        ExtraFields(int id) {
            this.id = id;
        }
    }


    public static LaneData createLaneData(String laneId, Map<Integer, String> props) {
        return createLaneData(laneId, props, null);
    }

    public static LaneData createLaneData(String laneId, Map<Integer, String> props, List<TraCIConnection> links) {
        if (laneId == null || props == null) return null;

        List<LaneData.Link> parsedLinks = links.stream()
            .map(connection ->
                new LaneData.Link(
                    connection.getApproachedLane(),
                    connection.getHasPrio(),
                    connection.getIsOpen(),
                    connection.getHasFoe(),
                    connection.getApproachedInternal(),
                    connection.getState(),
                    connection.getDirection(),
                    connection.getLength()
                ))
            .toList();

        return new LaneData(laneId,
            props.get(Fields.EDGE_ID.sumoPropertyId),
            SumoMapper.parseTraCIList(props.get(Fields.ALLOWED_VEHICLE_TYPES.sumoPropertyId)),
            SumoMapper.parseTraCIList(props.get(Fields.DISALLOWED_VEHICLE_TYPES.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.LENGTH.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.WIDTH.sumoPropertyId)),
            SumoMapper.parseShapeList(props.get(Fields.SHAPE.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_CO2_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_CO_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_HC_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_PMX_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_NOX_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_FUEL_CONSUMPTION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_ELECTRICITY_CONSUMPTION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_NOISE_DBA.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.VEHICLE_COUNT.sumoPropertyId)),
            SumoMapper.parseTraCIList(props.get(Fields.VEHICLE_IDS.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.TIME_OCCUPANCY_PERCENTAGE.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MEAN_VEHICLE_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MEAN_VEHICLE_LENGTH.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_WAITING_TIME.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.CURRENT_TRAVEL_DURATION.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.HALTING_VEHICLES_COUNT.sumoPropertyId)),
            SumoMapper.parseTraCIList(props.get(ExtraFields.LANE_CHANGE_ALLOWED_LEFT.id)),
            SumoMapper.parseTraCIList(props.get(ExtraFields.LANE_CHANGE_ALLOWED_RIGHT.id)),
            parsedLinks
        );
    }
}