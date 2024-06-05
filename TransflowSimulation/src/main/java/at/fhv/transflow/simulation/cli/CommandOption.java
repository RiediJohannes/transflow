package at.fhv.transflow.simulation.cli;

public enum CommandOption {
    DELAY("d", "delay"),
    INTERACTIVE("i", "interactive"),
    STEP_INCREMENT("s", "step-increment"),
    STEP_TIME("t", "step-t"),
    HELP("h", "help");


    public final String shortName;
    public final String fullName;

    CommandOption(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }
}