package TF2ClassWarsStatTracker.game;

public class LegacyConfigurationGrid {
    private final int[][][] mercenaryWins;  // [BLU Mercenary] [RED Mercenary] [{BLU Wins, RED Wins}]

    public LegacyConfigurationGrid() {
        mercenaryWins = new int[9][9][2];
    }

    public LegacyConfigurationGrid(int[][][] mercenaryWins) {
        this.mercenaryWins = mercenaryWins;
    }

    public void setMercenaryWins(int bluMercenary, int redMercenary, int bluWins, int redWins) {
        mercenaryWins[bluMercenary][redMercenary] = new int[] {bluWins, redWins};
    }

    public void incrementMercenaryWins(int team, int bluMercenary, int redMercenary) {
        mercenaryWins[bluMercenary][redMercenary][team]++;
    }

    public void decrementMercenaryWins(int team, int bluMercenary, int redMercenary) {
        mercenaryWins[bluMercenary][redMercenary][team]--;
    }

    public int[][][] getMatchupWins() {
        return mercenaryWins;
    }

    public int[] getMatchupWins(int bluMercenary, int redMercenary) {
        return mercenaryWins[bluMercenary][redMercenary];
    }

    public static LegacyConfigurationGrid getEmptyGrid() {
        return new LegacyConfigurationGrid();
    }
}