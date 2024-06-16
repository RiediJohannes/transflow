package at.fhv.transflow.simulation.sumo;

import org.eclipse.sumo.libsumo.Simulation;
import org.eclipse.sumo.libsumo.StringVector;

import java.util.Iterator;
import java.util.Locale;


/**
 * <p>Control environment for a traffic simulation powered by <a href="https://sumo.dlr.de/docs/index.html">SUMO</a>.
 * Starts and closes a SUMO simulation configured in a <em>*.sumocfg</em> file.</p><br>
 * <p>Requires the sumo command line tool to be installed on the system.</p>
 */
public class SumoSimulation implements Iterable<SumoStep>, AutoCloseable {
    private final SumoStepIterator stepIterator;
    private final String simName;

    /**
     * Instantiates a wrapper around a new SUMO simulation as defined by the given <code>sumocfg</code> file.
     */
    public SumoSimulation(SimulationOptions options) throws SumoConfigurationException {
        // load additional libraries if needed
//        System.loadLibrary("iconv-2");
//        System.loadLibrary("intl-8");
//        System.loadLibrary("proj_9_0");
        System.loadLibrary("libsumojni");

        // if no custom name was specified, take the name of the config file as this simulation's name (without file extension)
        this.simName = options.getSimulationRunName().orElse(
            options.getSimConfigPath().getFileName().toString().replaceFirst("[.][^.]+$", "")
        );

        // start the simulation
        Simulation.start(new StringVector(new String[]{"sumo", "-v",
            "-c", options.getSimConfigPath().toString(),
            "--step-length", String.format(Locale.US, "%.3f", options.getStepMillis() / 1000.0)}));

        stepIterator = new SumoStepIterator(options.getStepMillis(), options.getStepIncrement());
    }


    /**
     * The name of this simulation which is just the name of its config file.
     * @return The name of the loaded <code>*.sumocgf</code> file without file extension.
     */
    public String getName() {
        return simName;
    }

    /**
     * Execute a given number of steps from the current step in the loaded simulation.
     * @param numberOfSteps The number of steps to perform.
     * @return The new current {@link SumoStep} after performing the requested number of steps.
     */
    public SumoStep executeSteps(int numberOfSteps) {
        return stepIterator.executeSteps(numberOfSteps);
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
        return stepIterator.setCurrentMillis(targetStep);
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
        private final int stepIncrement;
        private final int stepMillis;
        private int currentMillis;

        public SumoStepIterator(int stepMillis, int stepIncrement) throws SumoConfigurationException {
            if (stepIncrement < 1) {
                throw new SumoConfigurationException("Step increment must be a positive integer! Given: " + stepIncrement);
            }
            if (stepMillis < 1 || stepMillis > 1000) {
                throw new SumoConfigurationException("Step length must be in the range of [1, 1000] milliseconds! " +
                    "Given: " + stepMillis);
            }

            this.stepIncrement = stepIncrement;
            this.stepMillis = stepMillis;
            this.currentMillis = -1;
        }

        @Override
        public SumoStep next() {
            // do not trigger a simulation step for the first next() call to retrieve data before the start of the simulation
            if (currentMillis == -1) {
                currentMillis = 0;
                return new SumoStep(currentMillis);
            }

            return executeSteps(stepIncrement);
        }

        @Override
        public boolean hasNext() {
            return Simulation.getMinExpectedNumber() > 0;
        }


        public int getCurrentMillis() {
            return currentMillis;
        }

        public SumoStep setCurrentMillis(int millis) {
            if (millis % stepMillis != 0) {
                throw new IllegalArgumentException("Manually requested step must be " +
                    "a multiple of the defined step increment [" + stepIncrement + "]!");
            }

            // sumo only allows to step forwards, not backwards
            if (millis > currentMillis) {
                currentMillis = millis;
                Simulation.step(millis / 1000.0);
            }

            return new SumoStep(getCurrentMillis());
        }

        public SumoStep executeSteps(int numberOfSteps) {
            for (int s = 0; s < numberOfSteps; s++) {
                currentMillis += stepMillis;
                Simulation.step(currentMillis / 1000.0); // let SUMO perform the step
            }

            return new SumoStep(getCurrentMillis());
        }
    }
}