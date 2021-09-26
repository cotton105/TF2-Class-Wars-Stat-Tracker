package TF2ClassWarsStatTracker.util;

public class Calculate {
    public static float getRatioBias(float x, float y) {
        if (x != 0 && y == 0)
            return -1;
        else
            return (1-x/y)/(1+x/y);
    }
}
