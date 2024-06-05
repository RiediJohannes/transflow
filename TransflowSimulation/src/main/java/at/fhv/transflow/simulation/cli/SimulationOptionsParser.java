package at.fhv.transflow.simulation.cli;

import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import static at.fhv.transflow.simulation.cli.CommandOption.*;


public class SimulationOptionsParser extends DefaultParser {
    private final HelpFormatter formatter = new HelpFormatter();
    private final Options options = new Options();
    private final String commandName;

    public SimulationOptionsParser(String commandName) {
        super(false);
        this.commandName = commandName;

        options.addOption(Option.builder(DELAY.shortName).longOpt(DELAY.fullName)
            .desc("Artificially slows down the simulation execution to have a minimum of the specified " +
                "delay <millis> between each simulation step.")
            .hasArg().argName("millis").type(Integer.class)
            .build()
        );

        options.addOption(Option.builder(INTERACTIVE.shortName).longOpt(INTERACTIVE.fullName)
            .desc("Waits for a continue command before resuming the simulation. If <sync-step> is given, " +
                "this number of steps is executed before waiting for an acknowledgement (defaults to 1 step).")
            .hasArg().argName("sync-step").type(Integer.class).optionalArg(true)
            .build()
        );

        options.addOption(Option.builder(STEP_INCREMENT.shortName).longOpt(STEP_INCREMENT.fullName)
            .desc("The number of simulation steps to execute before publishing metrics " +
                "of the current simulation state (default: 1; numbers smaller than 1 will fallback to default).")
            .hasArg().argName("count").type(Integer.class)
            .build()
        );

        options.addOption(Option.builder(STEP_TIME.shortName).longOpt(STEP_TIME.fullName)
            .desc("The duration of a step within the simulation scenario in milliseconds. " +
                "Value must be in range of [1-1000] milliseconds (default: 1000).")
            .hasArg().argName("millis").type(Integer.class)
            .build()
        );
    }


    public SimulationOptions parse(String[] arguments) throws SystemError {
        SimulationOptions simOptions = new SimulationOptions();

        try {
            CommandLine cmd = parse(this.options, arguments, false);

            if (cmd.hasOption(DELAY.shortName)) {
                simOptions.setDelayMillis(cmd.getParsedOptionValue(DELAY.shortName));
            }

            if (cmd.hasOption(INTERACTIVE.shortName)) {
                String optionalValue = cmd.getOptionValue(INTERACTIVE.shortName);
                int interactionInterval = optionalValue != null ? Integer.parseInt(optionalValue) : 1;
                simOptions.setInteractionInterval(interactionInterval);
            }

            if (cmd.hasOption(STEP_INCREMENT.shortName)) {
                simOptions.setStepIncrement(cmd.getParsedOptionValue(STEP_INCREMENT.shortName));
            }

            if (cmd.hasOption(STEP_TIME.shortName)) {
                simOptions.setStepMillis(cmd.getParsedOptionValue(STEP_TIME.shortName));
            }

        } catch (ParseException exp) {
            String help = showHelp();
            throw new SystemError(ErrorCode.INVALID_CLI_ARGUMENTS, exp.getMessage() + "\n\n" + help);
        } catch (NumberFormatException exp) {
            throw new SystemError(ErrorCode.INVALID_CLI_ARGUMENTS,
                "Expected an integer argument - " + exp.getMessage());
        }

        return simOptions;
    }

    public String showHelp() {
        StringWriter help = new StringWriter();
        PrintWriter writer = new PrintWriter(help);

        formatter.printHelp(
            writer, 110, commandName,
            """
                Runs a SUMO simulation with the specified simulation scenario and continuously publishes its outcomes.
                    
                Options:""",
            options, 3, 3, null
        );

        writer.flush();
        return help.toString();
    }
}