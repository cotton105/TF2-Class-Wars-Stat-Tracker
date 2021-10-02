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

    private static Color getColourScaledFromWhite(float weight, Color colour) {
        int[] weightedRGBArray = new int[3];
        int[] diffFromWhite = new int[3];
        diffFromWhite[0] = 255 - colour.getRed();
        diffFromWhite[1] = 255 - colour.getGreen();
        diffFromWhite[2] = 255 - colour.getBlue();
        weightedRGBArray[0] = 255 - (int)(Math.abs(weight) * diffFromWhite[0]);
        weightedRGBArray[1] = 255 - (int)(Math.abs(weight) * diffFromWhite[1]);
        weightedRGBArray[2] = 255 - (int)(Math.abs(weight) * diffFromWhite[2]);
        return new Color(weightedRGBArray[0], weightedRGBArray[1], weightedRGBArray[2]);
    }
}
