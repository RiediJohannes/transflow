package at.fhv.transflow.simulation.sumo.data;

/**
 * Represents a single object in a SUMO simulation. This interface is mainly used as a marker interface
 * to use different SUMO DTOs in collections and return statements. Implementations of this interface are
 * only required to provide a public {@link SumoObject#id()}, which is supposed to be the unique ID of the
 * object inside its SUMO simulation of origin.
 */
public interface SumoObject {

    /**
     * Unique ID of object inside its SUMO simulation of origin.
     * @return The unchanged string value of the ID.
     */
    String id();
}