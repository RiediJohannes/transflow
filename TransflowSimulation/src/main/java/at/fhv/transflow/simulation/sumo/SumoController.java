package at.fhv.transflow.simulation.sumo;

import at.fhv.transflow.simulation.cli.ErrorCode;
import at.fhv.transflow.simulation.cli.SystemError;
import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.JsonMapper;
import at.fhv.transflow.simulation.messaging.MessagingException;
import at.fhv.transflow.simulation.sumo.data.SumoObject;
import at.fhv.transflow.simulation.sumo.mapping.*;
import at.fhv.transflow.simulation.utils.AwaitableExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.sumo.libsumo.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.stream.Stream;


public class SumoController {
    private static final int THREAD_POOL_SIZE = 20;
    private final SumoSimulation simulation;
    private final IMessagingService messagingService;
    private Instant startTime;

    public SumoController(SumoSimulation simulation, IMessagingService messagingService) {
        this.simulation = simulation;
        this.messagingService = messagingService;
    }


    /**
     * A timestamp of the local system time when the current simulation run was started (assuming that is has already been started).
     * @return An {@link Optional<Instant>} of the time {@link #runSimulation(String, String, int)} was called or an empty Optional
     * if the simulation has not been started yet.
     */
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    /**
     * A unique ID of the simulation run consisting of the sim config's filename and the timestamp
     * when the simulation was started in <strong>ISO-8601 format</strong> and <strong>UTC time zone</strong>.
     * @return A string representation of <code>[filename (without extension)]@[ISO-8601-timestamp]</code>.
     */
    public String getId() {
        // [simName]@[ISO-8601-timestamp] or else [simName]@ready
        return simulation.getName() + "@" + getStartTime()
            .map(Instant::toString)
            .orElse("ready");
    }

    /**
     * Executes the {@link SumoSimulation} (i.e. running its configured simulation scenario) and publishes
     * the simulation's current state through the specified {@link IMessagingService} (both given during instantiation).
     * Data is published regularly in intervals specified in the {@link SumoSimulation} instance to the given message topic,
     * consisting of {@code rootTopic/simRunId/subTopic/domainTopic/stepId}, where:
     * <ul>
     *   <li>{@code rootTopic} and {@code subTopic} are method parameters</li>
     *   <li>{@code simRunId} is gathered by {@link SumoController#getId() SumoController.getId()}</li>
     *   <li>{@code domainTopic} specifies the category of data (vehicle data, lane data, ...)</li>
     *   <li>{@code stepId} is gathered by {@link SumoStep#getCurrentMillis()}</li>
     * </ul>
     * @param rootTopic   The highest topic level which all data of the simulation is sent to.
     * @param subTopic    A sublevel of the topic hierarchy allowing to further specify where metrics of the
     *                    simulation run are sent to.
     * @param delayMillis The minimum duration of the evaluation of one simulation step in milliseconds. If the
     *                    system is faster than this, an artificial delay is added in order to not overwhelm the
     *                    messaging service or its clients.
     * @throws SystemError Thrown if the simulation run is aborted.
     */
    public void runSimulation(String rootTopic, String subTopic, int delayMillis) throws SystemError {
        startTime = Instant.now();
        String metricsTopic = rootTopic + "/" + getId() + "/" + subTopic;

        // subscribe to all properties of interest for every edge (road connection) in the simulation
        VehicleType.getIDList().forEach(type -> VehicleType.subscribe(type, new IntVector(VehicleTypeMapper.Fields.sumoProperties())));
        Edge.getIDList().forEach(edge -> Edge.subscribe(edge, new IntVector(EdgeMapper.Fields.sumoProperties())));
        Route.getIDList().forEach(route -> Route.subscribe(route, new IntVector(RouteMapper.Fields.sumoProperties())));
        Lane.getIDList().forEach(lane -> Lane.subscribe(lane, new IntVector(LaneMapper.Fields.sumoProperties())));
        Junction.getIDList().forEach(junction -> Junction.subscribe(junction, new IntVector(JunctionMapper.Fields.sumoProperties())));

        System.out.println();
        try (AwaitableExecutor executor = new AwaitableExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE))) {
            for (SumoStep step : simulation) {
                System.out.printf("Current simulation time: %.2fs\r", step.getCurrentMillis() / 1000.0);

                executor.reset();
                long stepStartTime = System.currentTimeMillis();

                // subscribe to all properties of interest for every vehicle newly loaded (updates every time step)
                for (String newVehicleId : Simulation.getLoadedIDList()) {
                    Vehicle.subscribe(newVehicleId, new IntVector(VehicleMapper.Fields.sumoProperties()));
                    Vehicle.subscribeLeader(newVehicleId, 200.0); // leader can only be subscribed via this method
                }

                // collect metrics
                Map<String, Stream<? extends SumoObject>> topicMap = new HashMap<>();
                topicMap.put("vehicles", step.getVehicleData());
                topicMap.put("vehicle_types", step.getVehicleTypeData());
                topicMap.put("lanes", step.getLaneData());
                topicMap.put("edges", step.getEdgeData());
                topicMap.put("routes", step.getRouteData());
                topicMap.put("junctions", step.getJunctionData());

                topicMap.forEach((domainTopic, metrics) -> {
                    // message topic included the domain-related topic name as well as the current simulation time step
                    final String topic = metricsTopic + "/" + domainTopic + "/" + step.getCurrentMillis();

                    metrics.parallel().forEach(payload ->
                        executor.execute(() -> {
                            try {
                                // serialize a single SumoObject to JSON and send it via the messaging service
                                byte[] jsonPayload = JsonMapper.instance().toJsonBytes(payload);
                                messagingService.sendMessage(topic, jsonPayload, 1);
                            } catch (JsonProcessingException exp) {
                                System.err.printf("""
                                    Failed to parse object with ID %s of domain %s in time step %s;
                                    Reason: %s
                                    """, payload.id(), domainTopic, step.getCurrentMillis(), exp.getMessage());
                            } catch (MessagingException exp) {
                                System.err.println(exp.getMessage());
                            }
                        })
                    );
                });

                // wait until every thread spawned in this time step has finished its execution
                executor.awaitCompletion();

                // guarantee the step execution time to be at minimum as long as specified by delayMillis
                long stepDuration = System.currentTimeMillis() - stepStartTime;
                if (stepDuration < delayMillis) {
                    Thread.sleep(delayMillis - stepDuration);
                }
            }
        } catch (InterruptedException exp) {
            throw new SystemError(ErrorCode.EXECUTION_INTERRUPTED);
        }
    }
}