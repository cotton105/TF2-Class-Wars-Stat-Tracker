package TF2ClassWarsStatTracker.util;

import java.awt.*;

public class Calculate {
    public static float getRatioBias(float x, float y) {
        if (x != 0 && y == 0)
            return -1;
        else
            return (1-x/y)/(1+x/y);
    }

    public static Color getColourScaledFromWhite(float weight, Color colour1, Color colour2) {
        return (weight < 0) ? getColourScaledFromWhite(weight, colour1) : getColourScaledFromWhite(weight, colour2);
    }

    public static Color getColourHighlight(Color colour, Color highlightColour) {
        return getColourHighlight(colour, highlightColour, 0.1f);
    }

    public static Color getColourHighlight(Color colour, Color highlightColour, float weight) {
        int[] colourDifference = getColourDifference(colour, highlightColour);
        int[] weightedRGBArray = new int[3];
        weightedRGBArray[0] = highlightColour.getRed() - (int)((1-weight) * colourDifference[0]);
        weightedRGBArray[1] = highlightColour.getGreen() - (int)((1-weight) * colourDifference[1]);
        weightedRGBArray[2] = highlightColour.getBlue() - (int)((1-weight) * colourDifference[2]);
        return new Color(weightedRGBArray[0], weightedRGBArray[1], weightedRGBArray[2]);
    }

    private static Color getColourScaledFromWhite(float weight, Color colour) {
        int[] weightedRGBArray = new int[3];
        int[] diffFromWhite = getColourDifference(colour, Color.WHITE);
        weightedRGBArray[0] = 255 - (int)(Math.abs(weight) * diffFromWhite[0]);
        weightedRGBArray[1] = 255 - (int)(Math.abs(weight) * diffFromWhite[1]);
        weightedRGBArray[2] = 255 - (int)(Math.abs(weight) * diffFromWhite[2]);
        return new Color(weightedRGBArray[0], weightedRGBArray[1], weightedRGBArray[2]);
    }

    private static int[] getColourDifference(Color colour1, Color colour2) {
        int[] colourDifference = new int[3];
        colourDifference[0] = colour2.getRed() - colour1.getRed();
        colourDifference[1] = colour2.getGreen() - colour1.getGreen();
        colourDifference[2] = colour2.getBlue() - colour1.getBlue();
        return colourDifference;
    }
}
