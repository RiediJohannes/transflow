package at.fhv.transflow.simulation.cli;

public class SimulationOptions {
    // initialized with default values
    private int delayMillis = 0;
    private int stepIncrement = 1;
    private int interactionInterval = 0;
    private int stepMillis = 1000;

    public SimulationOptions() {
    }


    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }


    public int getStepIncrement() {
        return stepIncrement;
    }

    public void setStepIncrement(int stepIncrement) {
        this.stepIncrement = stepIncrement;
    }


    public int getInteractionInterval() {
        return interactionInterval;
    }

    public void setInteractionInterval(int interactionInterval) {
        this.interactionInterval = interactionInterval;
    }

    public boolean isInteractive() {
        return this.interactionInterval < 1;
    }


    public int getStepMillis() {
        return stepMillis;
    }

    public void setStepMillis(int stepMillis) {
        this.stepMillis = stepMillis;
    }
}