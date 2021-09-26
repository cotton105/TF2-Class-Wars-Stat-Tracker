package TF2ClassWarsStatTracker.util;

public class Calculation {
    public static float getRatioBias(float x, float y) {
        return (1-x/y)/(1+x/y);
    }
}
