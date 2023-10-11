package at.fhv.transflow.simulation.data;

import org.eclipse.sumo.libsumo.TraCIColor;

import java.awt.*;

public class SumoMapper {

    private SumoMapper() {}

    public static Color colorFromTraCI(TraCIColor traciColor) {
        return new Color(
                traciColor.getR(),
                traciColor.getG(),
                traciColor.getB(),
                traciColor.getB()
        );
    }
}