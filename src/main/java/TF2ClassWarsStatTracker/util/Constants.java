package TF2ClassWarsStatTracker.util;

import java.awt.*;

public class Constants {
    public static final int
            BLU = 0, RED = 1,
            OVERALL = 0, NORMAL = 1, GLOBAL_ROLLS = 2, MADNESS = 3, MULTIPLY_WEAPONS_STATS = 4, GOOD_ROLLS = 5;
    public static final Color
            BLU_COLOUR = new Color(171,203,255),
            RED_COLOUR = new Color(255,125,125);
    public static final String[]
            MERCENARY = {
                "Scout", "Soldier", "Pyro",
                "Demoman", "Heavy", "Engineer",
                "Medic", "Sniper", "Spy"},
            TEAM = {
                "BLU", "RED"},
            GAME_MODES = new String[] {
                "Overall", "Normal", "Global Rolls",
                "Madness", "Multiply Weapons' Stats",
                "Good Rolls"};
}
