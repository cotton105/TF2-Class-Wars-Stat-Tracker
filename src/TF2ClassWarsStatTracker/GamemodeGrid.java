package TF2ClassWarsStatTracker;

public class GamemodeGrid {
    public final static int NORMAL = 0, GLOBAL_ROLLS = 1, MADNESS = 2, MULTIPLY_WEAPONS_STATS = 3, GOOD_ROLLS = 4;
    private Integer[][][] mercenaryWins;  // [BLU Mercenary] [RED Mercenary] [{BLU Wins, RED Wins}]

    public GamemodeGrid() {
        mercenaryWins = new Integer[9][9][2];
    }

    public void setMercenaryWins(int bluMercenary, int redMercenary, int bluWins, int redWins) {
        mercenaryWins[bluMercenary][redMercenary] = new Integer[] {bluWins, redWins};
    }

    public void incrementMercenaryWins(int team, int bluMercenary, int redMercenary) {
        mercenaryWins[bluMercenary][redMercenary][team]++;
    }

    public Integer[][][] getMercenaryWins() {
        return mercenaryWins;
    }
}
