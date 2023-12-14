package at.fhv.transflow.simulation.sumo.mapping;

import at.fhv.transflow.simulation.sumo.data.VehicleTypeData;
import org.eclipse.sumo.libsumo.Constants;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class VehicleTypeMapper {
    /**
     * Inner enum of all the fields (properties) of a {@link VehicleTypeData} record. Each enum constant
     * contains the integer ID of the respective SUMO vehicle type property, which is paramount to know in
     * order to handle variable subscriptions via TraCI (both for subscribing and parsing).
     * @see <a href="https://sumo.dlr.de/docs/TraCI/VehicleType_Value_Retrieval.html">SUMO vehicle type properties</a>
     */
    public enum Fields {
        VEHICLE_CLASS(Constants.VAR_VEHICLECLASS),
        LENGTH(Constants.VAR_LENGTH),
        WIDTH(Constants.VAR_WIDTH),
        HEIGHT(Constants.VAR_HEIGHT),
        SHAPE(Constants.VAR_SHAPECLASS),
        HEX_COLOR(Constants.VAR_COLOR),
        MAX_SPEED(Constants.VAR_MAXSPEED),
        MAX_SPEED_LATERAL(Constants.VAR_MAXSPEED_LAT),
        MAX_ACCELERATION(Constants.VAR_ACCEL),
        MAX_DECELERATION(Constants.VAR_DECEL),
        SPEED_FACTOR(Constants.VAR_SPEED_FACTOR),
        SPEED_DEVIATION(Constants.VAR_SPEED_DEVIATION),
        EMISSION_CLASS(Constants.VAR_EMISSIONCLASS),
        SCALE(Constants.VAR_SCALE),
        TAU(Constants.VAR_TAU),
        SIGMA(Constants.VAR_IMPERFECTION),
        MIN_GAP(Constants.VAR_MINGAP),
        MIN_LATERAL_GAP(Constants.VAR_MINGAP_LAT),
        LATERAL_ALIGNMENT(Constants.VAR_LATALIGNMENT),
        ACTION_STEP_LENGTH(Constants.VAR_ACTIONSTEPLENGTH),
        PERSON_CAPACITY(Constants.VAR_PERSON_CAPACITY),
        BOARDING_DURATION(Constants.VAR_BOARDING_DURATION);


        public final int sumoPropertyId;

        Fields(int sumoPropertyId) {
            this.sumoPropertyId = sumoPropertyId;
        }

        /**
         * Get a set of all the sumo property IDs in the {@link Fields} enum.
         * These can be used to batch-subscribe to a ton of specific vehicle type properties via TraCI.
         * @return A set of the integer ID of every SUMO vehicle type property of interest.
         */
        public static Set<Integer> sumoProperties() {
            return Arrays.stream(values())
                .map(field -> field.sumoPropertyId)
                .collect(Collectors.toSet());
        }
    }


    public static VehicleTypeData createVehicleData(String vehicleTypeId, Map<Integer, String> props) {
        if (vehicleTypeId == null || props == null) return null;

        return new VehicleTypeData(vehicleTypeId,
            props.get(Fields.VEHICLE_CLASS.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.LENGTH.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.WIDTH.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.HEIGHT.sumoPropertyId)),
            SumoMapper.parseShapeList(props.get(Fields.SHAPE.sumoPropertyId)),
            props.get(Fields.HEX_COLOR.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_SPEED.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_SPEED_LATERAL.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_ACCELERATION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MAX_DECELERATION.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SPEED_FACTOR.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SPEED_DEVIATION.sumoPropertyId)),
            props.get(Fields.EMISSION_CLASS.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.SCALE.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.TAU.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.SIGMA.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MIN_GAP.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.MIN_LATERAL_GAP.sumoPropertyId)),
            props.get(Fields.LATERAL_ALIGNMENT.sumoPropertyId),
            SumoMapper.tryParseDouble(props.get(Fields.ACTION_STEP_LENGTH.sumoPropertyId)),
            SumoMapper.tryParseInteger(props.get(Fields.PERSON_CAPACITY.sumoPropertyId)),
            SumoMapper.tryParseDouble(props.get(Fields.BOARDING_DURATION.sumoPropertyId))
        );
    }
}