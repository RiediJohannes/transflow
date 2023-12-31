package at.fhv.transflow.simulation.sumo;

import at.fhv.transflow.simulation.messaging.IMessagingService;
import at.fhv.transflow.simulation.messaging.JsonMapper;
import at.fhv.transflow.simulation.messaging.MessagingException;
import at.fhv.transflow.simulation.sumo.mapping.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.eclipse.sumo.libsumo.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class SumoController {
    private final SumoSimulation simulation;
    private final IMessagingService messagingService;
    private LocalDateTime startTime;

    public SumoController(SumoSimulation simulation, IMessagingService messagingService) {
        this.simulation = simulation;
        this.messagingService = messagingService;
    }


    /**
     * A timestamp of the local system time when the current simulation run was started (assuming that is has already been started).
     * @return An {@link Optional<LocalDateTime>} of the time {@link #runSimulation(String, String)} was called or an empty Optional
     * if the simulation has not been started yet.
     */
    public Optional<LocalDateTime> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    /**
     * A unique ID of the simulation run consisting of it sim config's filename and the timestamp
     * when the simulation was started.
     * @return A string representation of <code>[filename (without extension)]@[ISO-timestamp]</code>.
     */
    public String getId() {
        // [simName]@[ISO-date] or else [simName]@ready
        return simulation.getName() + "@" + getStartTime()
            .map(time -> time.format(DateTimeFormatter.ISO_DATE_TIME))
            .orElse("ready");
    }

    public void runSimulation(String rootTopic, String subTopic) {
        startTime = LocalDateTime.now();
        String topic = rootTopic + "/" + getId() + "/" + subTopic;

        // subscribe to all properties of interest for every edge (road connection) in the simulation
        VehicleType.getIDList().forEach(type -> VehicleType.subscribe(type, new IntVector(VehicleTypeMapper.Fields.sumoProperties())));
        Edge.getIDList().forEach(edge -> Edge.subscribe(edge, new IntVector(EdgeMapper.Fields.sumoProperties())));
        Route.getIDList().forEach(route -> Route.subscribe(route, new IntVector(RouteMapper.Fields.sumoProperties())));
        Lane.getIDList().forEach(lane -> Lane.subscribe(lane, new IntVector(LaneMapper.Fields.sumoProperties())));
        Junction.getIDList().forEach(junction -> Junction.subscribe(junction, new IntVector(JunctionMapper.Fields.sumoProperties())));

        for (SumoStep step : simulation) {
            // debug info
            System.out.println("Step: " + step.getId());
            System.out.println("NumVehicles: " + step.getVehicleCount());

            // subscribe to all properties of interest for every freshly loaded vehicle
            for (String newVehicleId : Simulation.getLoadedIDList()) {
                Vehicle.subscribe(newVehicleId, new IntVector(VehicleMapper.Fields.sumoProperties()));
                Vehicle.subscribeLeader(newVehicleId, 200.0); // leader can only be subscribed via this method
            }

            // collect metrics
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("vehicles", step.getVehicleData());
            metrics.put("vehicleTypes", step.getVehicleTypeData());
            metrics.put("lanes", step.getLaneData());
            metrics.put("edges", step.getEdgeData());
            metrics.put("routes", step.getRouteData());
            metrics.put("junctions", step.getJunctionData());

            try {
                byte[] jsonPayload = JsonMapper.instance().toJsonBytes(metrics);
                messagingService.sendMessage(topic, jsonPayload, 1);
            } catch (JsonProcessingException exp) {
                System.err.printf("Failed to parse objects in time step %s;\nReason: %s\n",
                    step.getId(), exp.getMessage());
            } catch (MessagingException exp) {
                System.err.printf(exp.getMessage());
            }
        }
    }
}