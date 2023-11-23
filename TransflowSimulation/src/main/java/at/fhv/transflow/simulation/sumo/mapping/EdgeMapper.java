package at.fhv.transflow.simulation.sumo.mapping;

import at.fhv.transflow.simulation.sumo.data.EdgeData;
import org.eclipse.sumo.libsumo.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class EdgeMapper {
    /**
     * Inner enum of all the fields (properties) of a {@link EdgeData} record. Each enum constant
     * contains the integer ID of the respective SUMO edge property, which is paramount to know in
     * order to handle variable subscriptions via TraCI (both for subscribing and parsing).
     * @see <a href="https://sumo.dlr.de/docs/TraCI/Edge_Value_Retrieval.html">SUMO edge properties</a>
     */
    public enum Fields {
        LANE_COUNT(Constants.VAR_LANE_INDEX),
        STREET_NAME(Constants.VAR_NAME),
        CURRENT_TRAVEL_DURATION(Constants.VAR_CURRENT_TRAVELTIME),
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
        MEAN_VEHICLE_LENGTH(Constants.LAST_STEP_LENGTH),
        SUM_WAITING_TIME(Constants.VAR_WAITING_TIME),
        PERSON_IDS(Constants.LAST_STEP_PERSON_ID_LIST),
        HALTING_VEHICLES_COUNT(Constants.LAST_STEP_VEHICLE_HALTING_NUMBER);


        public final int sumoPropertyId;

        Fields(int sumoPropertyId) {
            this.sumoPropertyId = sumoPropertyId;
        }

        /**
         * Get a set of all the sumo property IDs in the {@link Fields} enum.
         * These can be used to batch-subscribe to a ton of specific edge properties via TraCI.
         * @return A set of the integer ID of every SUMO edge property of interest.
         */
        public static Set<Integer> sumoProperties() {
            return Arrays.stream(values())
                .map(field -> field.sumoPropertyId)
                .collect(Collectors.toSet());
        }
    }


    public static EdgeData createEdgeData(String edgeId, Map<Integer, String> props) {
        if (edgeId == null || props == null) return null;

        String[] vehicleIds = SumoMapper.parseTraCIList(props.get(Fields.VEHICLE_IDS.sumoPropertyId));
        String[] personIds = SumoMapper.parseTraCIList(props.get(Fields.PERSON_IDS.sumoPropertyId));

        return new EdgeData(edgeId,
            SumoMapper.tryParseInteger(props.get(Fields.LANE_COUNT.sumoPropertyId)),
            props.get(Fields.STREET_NAME.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_CO2_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_CO_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_HC_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_PMX_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_NOX_MG_PER_SECOND.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_FUEL_CONSUMPTION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_ELECTRICITY_CONSUMPTION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_NOISE_DBA.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.VEHICLE_COUNT.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MEAN_VEHICLE_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MEAN_VEHICLE_LENGTH.sumoPropertyId)),
            vehicleIds,
            personIds,
            SumoMapper.tryParseDouble(props.get(Fields.TIME_OCCUPANCY_PERCENTAGE.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.CURRENT_TRAVEL_DURATION.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.HALTING_VEHICLES_COUNT.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SUM_WAITING_TIME.sumoPropertyId))
        );
    }
}