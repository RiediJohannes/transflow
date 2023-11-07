package at.fhv.transflow.simulation.sumo.data;

import org.eclipse.sumo.libsumo.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO for all data points of interest for a single vehicle at a given simulation step.
 */
public record VehicleData(
    // tags
    String id,
    String hexColor,
    String vehicleTypeId,
    double length,
    double width,
    double height,
    int personCapacity,
    double maxSpeed,
    double maxAcceleration,
    double maxDeceleration,
    double speedFactor,
    double speedDeviation,
    String shapeClass,
    double tau,
    double sigma,
    String routeId,
    double minFrontGap,
    double minLateralGap,

    // fields
    double speed,
    double acceleration,
    double lateralSpeed,
    double allowedSpeed,
    double angle,
    String roadId,
    int edgeInRoute,
    int lane,
    double PositionOnLane,
    int laneChangeState,
    int signalState,
    int stopState,
    List<String> personIds,
    double CO2mgPerSecond,
    double HCmgPerSecond,
    double PMXmgPerSecond,
    double NOXmgPerSecond,
    double fuelConsumption,
    double electricityConsumption,
    double noiseDBA,
    double totalDistance,
    double totalWaitingTime,
    double totalTimeLoss,
    double boardingDuration,
    String leaderVehicleId,
    double leaderVehicleDistance
) {
    // restrictions for constructor
    public VehicleData {
        Objects.requireNonNull(id);
    }

    /**
     * Inner enum of all the fields (properties) of a {@link VehicleData} record. Each enum constant
     * contains the integer ID of the respective SUMO vehicle property, which is paramount to know in
     * order to handle variable subscriptions via TraCI.
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
}