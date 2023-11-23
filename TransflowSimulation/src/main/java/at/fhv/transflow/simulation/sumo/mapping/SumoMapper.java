package at.fhv.transflow.simulation.sumo.mapping;

import org.eclipse.sumo.libsumo.TraCIColor;

import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Static helper class for general purpose mapping operations between SUMO-specific data types
 * and common Java data types.
 */
public abstract class SumoMapper {

    public static String hexColorFromTraCI(TraCIColor traciColor) {
        if (traciColor == null) return null;

        return String.format("#%02x%02x%02x%02x",
            traciColor.getR(), traciColor.getG(), traciColor.getB(), traciColor.getA());
    }

    public static String hexColorFromTraCI(String traciColorString) {
        if (traciColorString == null) return null;

        Pattern pattern = Pattern.compile("(?<=[(,\\s])\\d+(?=[,)\\s])");
        Matcher matcher = pattern.matcher(traciColorString);

        List<String> matches = matcher.results().map(MatchResult::group).toList();
        if (matches.size() == 4) {
            return String.format("#%02x%02x%02x%02x",
                Integer.parseInt(matches.get(0)),
                Integer.parseInt(matches.get(1)),
                Integer.parseInt(matches.get(2)),
                Integer.parseInt(matches.get(3)));
        }

        return null;
    }

    public static String[] parseTraCIList(String traciList) {
        if (traciList == null) return null;

        // return early if the list is empty to prevent it from being parsed as `new String[] {""}`
        if ("[]".equals(traciList.strip())) return new String[0];

        return traciList
            .replaceAll("[\\[\\]]", "")
            .split(",");
    }

    /**
     * Parses an Integer object from a given string using the {@link Integer#valueOf(String)} method
     * but catches {@link NumberFormatException} and prevents {@link NullPointerException} to return null
     * as an error value instead.
     * @param numberString The string to parsed to an Integer.
     * @return Either an Integer with the parsed value or null if failed to parse.
     */
    public static Integer tryParseInteger(String numberString) {
        if (numberString == null) return null;

        try {
            return Integer.valueOf(numberString);
        } catch (NumberFormatException exp) {
            return null;
        }
    }

    /**
     * Parses a Double object from a given string using the {@link Double#valueOf(String)} method
     * but catches {@link NumberFormatException} and prevents {@link NullPointerException} to return null
     * as an error value instead.
     * @param numberString The string to parsed to a Double.
     * @return Either a Double with the parsed value or null if failed to parse.
     */
    public static Double tryParseDouble(String numberString) {
        if (numberString == null) return null;

        try {
            return Double.valueOf(numberString);
        } catch (NumberFormatException exp) {
            return null;
        }
    }
}