package at.fhv.transflow.simulation.sumo;

import at.fhv.transflow.simulation.ErrorCode;
import at.fhv.transflow.simulation.SystemError;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;


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
     * @return An {@link Optional<Instant>} of the time {@link #runSimulation(String, String)} was called or an empty Optional
     * if the simulation has not been started yet.
     */
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    /**
     * A unique ID of the simulation run consisting of it sim config's filename and the timestamp
     * when the simulation was started in <strong>ISO-8601 format</strong> and <strong>UTC time zone</strong>.
     * @return A string representation of <code>[filename (without extension)]@[ISO-8601-timestamp]</code>.
     */
    public String getId() {
        // [simName]@[ISO-8601-timestamp] or else [simName]@ready
        return simulation.getName() + "@" + getStartTime()
            .map(Instant::toString)
            .orElse("ready");
    }

    public void runSimulation(String rootTopic, String subTopic) throws SystemError {
        startTime = Instant.now();
        String metricsTopic = rootTopic + "/" + getId() + "/" + subTopic;

        // subscribe to all properties of interest for every edge (road connection) in the simulation
        VehicleType.getIDList().forEach(type -> VehicleType.subscribe(type, new IntVector(VehicleTypeMapper.Fields.sumoProperties())));
        Edge.getIDList().forEach(edge -> Edge.subscribe(edge, new IntVector(EdgeMapper.Fields.sumoProperties())));
        Route.getIDList().forEach(route -> Route.subscribe(route, new IntVector(RouteMapper.Fields.sumoProperties())));
        Lane.getIDList().forEach(lane -> Lane.subscribe(lane, new IntVector(LaneMapper.Fields.sumoProperties())));
        Junction.getIDList().forEach(junction -> Junction.subscribe(junction, new IntVector(JunctionMapper.Fields.sumoProperties())));

        try (AwaitableExecutor executor = new AwaitableExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE))) {
            for (SumoStep step : simulation) {
                // debug info
                System.out.println("Step: " + step.getId());

                // subscribe to all properties of interest for every freshly loaded vehicle
                for (String newVehicleId : Simulation.getLoadedIDList()) {
                    Vehicle.subscribe(newVehicleId, new IntVector(VehicleMapper.Fields.sumoProperties()));
                    Vehicle.subscribeLeader(newVehicleId, 200.0); // leader can only be subscribed via this method
                }

                // collect metrics
                Map<String, List<? extends SumoObject>> topicMap = new HashMap<>();
                topicMap.put("vehicles", step.getVehicleData());
                topicMap.put("vehicle_types", step.getVehicleTypeData());
                topicMap.put("lanes", step.getLaneData());
                topicMap.put("edges", step.getEdgeData());
                topicMap.put("routes", step.getRouteData());
                topicMap.put("junctions", step.getJunctionData());

                topicMap.forEach((domainTopic, metrics) -> {
                    // message topic included the domain-related topic name as well as the current simulation time step
                    final String topic = metricsTopic + "/" + domainTopic + "/" + step.getId();

                    metrics.stream().parallel().forEach(payload ->
                        executor.execute(() -> {
                            try {
                                // serialize a single SumoObject to JSON and send it via the messaging service
                                byte[] jsonPayload = JsonMapper.instance().toJsonBytes(payload);
                                messagingService.sendMessage(topic, jsonPayload, 1);
                            } catch (JsonProcessingException exp) {
                                System.err.printf("""
                                    Failed to parse object with ID %s of domain %s in time step %s;
                                    Reason: %s
                                    """, payload.id(), domainTopic, step.getId(), exp.getMessage());
                            } catch (MessagingException exp) {
                                System.err.println(exp.getMessage());
                            }
                        })
                    );
                });

                // wait until every thread spawned in this time step has finished its execution
                executor.awaitCompletion();
            }
        } catch (InterruptedException exp) {
            throw new SystemError(ErrorCode.EXECUTION_INTERRUPTED);
        }
    }
}