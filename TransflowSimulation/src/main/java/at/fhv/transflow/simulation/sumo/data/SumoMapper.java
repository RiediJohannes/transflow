package at.fhv.transflow.simulation.sumo.data;

import org.eclipse.sumo.libsumo.TraCIColor;

public abstract class SumoMapper {

    private SumoMapper() {}

    public static String hexColorFromTraCI(TraCIColor traciColor) {
        return String.format("#%02x%02x%02x%02x",
            traciColor.getR(), traciColor.getG(), traciColor.getB(), traciColor.getA());
    }
}