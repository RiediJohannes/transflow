package at.fhv.transflow.simulation.sumo;

import org.eclipse.sumo.libsumo.Simulation;
import org.eclipse.sumo.libsumo.StringVector;

import java.nio.file.Path;
import java.util.Iterator;

/**
 * <p>Control environment for a traffic simulation powered by <a href="https://sumo.dlr.de/docs/index.html">SUMO</a>.
 * Starts and closes a SUMO simulation configured in a <em>*.sumocfg</em> file.</p><br>
 * <p>Requires the sumo command line tool to be installed on the system.</p>
 */
public class SumoSimulation implements Iterable<SumoStep>, AutoCloseable {
    private final SumoStepIterator stepIterator;

    /**
     * Instantiates the controller for a new SUMO simulation as defined by the given sumocfg file.
     * @param simulationConfig Relative path to the simulation configuration file with the file extension <em>.sumocfg</em>.
     */
    public SumoSimulation(Path simulationConfig) {
        // load additional libraries if needed
//        System.loadLibrary("iconv-2");
//        System.loadLibrary("intl-8");
//        System.loadLibrary("proj_9_0");
        System.loadLibrary("libsumojni");

        // start the simulation
        Simulation.start(new StringVector(new String[]{"sumo", "-c", simulationConfig.toString()}));
        stepIterator = new SumoStepIterator();
    }


    /**
     * Execute a given number of steps from the current step in the loaded simulation.
     * @param numberOfSteps The number of steps to perform.
     * @return The new current {@link SumoStep} after performing the requested number of steps.
     */
    public SumoStep executeSteps(int numberOfSteps) {
        return skipToStep(stepIterator.getStep() + numberOfSteps);
    }

    /**
     * Executes every subsequent simulation step up until the given target step.<br>
     * This method only allows to step forward in the simulation timeline as SUMO inherently does not allow
     * to jump back in time.
     * @param targetStep Requested step up until which the simulation should proceed computing. If smaller or equal to the current
     *                   simulation step, nothing will happen.
     * @return The new current {@link SumoStep} after reaching the target step, i.e., the target step itself.
     */
    public SumoStep skipToStep(int targetStep) {
        return stepIterator.setStep(targetStep);
    }

    @Override
    public void close() {
        Simulation.close();
    }

    @Override
    public Iterator<SumoStep> iterator() {
        return stepIterator;
    }


    /**
     * Inner class to handle iteration over every simulation step ({@link SumoStep}) in the loaded SUMO traffic simulation.
     */
    private static class SumoStepIterator implements Iterator<SumoStep> {
        private int currentStep = 0;

        @Override
        public SumoStep next() {
            Simulation.step();
            return new SumoStep(currentStep++);
        }

        @Override
        public boolean hasNext() {
            return Simulation.getMinExpectedNumber() > 0;
        }


        public int getStep() {
            return currentStep;
        }

        public SumoStep setStep(int step) {
            // sumo only allows to step forwards, not backwards
            if (step > currentStep) {
                currentStep = step;
                Simulation.step(step);
            }
            return new SumoStep(currentStep);
        }
    }
}