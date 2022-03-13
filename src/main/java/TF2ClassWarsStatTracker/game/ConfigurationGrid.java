package TF2ClassWarsStatTracker.game;

public class ConfigurationGrid {
    private String mapName;
    private Integer stageNumber;
    private String gameModeName;
    private final int[][][] mercenaryWins;  // [BLU Mercenary] [RED Mercenary] [{BLU Wins, RED Wins}]

    public ConfigurationGrid() {
        mercenaryWins = new int[9][9][2];
    }

    public ConfigurationGrid(int[][][] mercenaryWins) {
        this.mercenaryWins = mercenaryWins;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getStageNumber() {
        return stageNumber;
    }

    public void setStageNumber(int stageNumber) {
        this.stageNumber = stageNumber;
    }

    public String getGameModeName() {
        return gameModeName;
    }

    public void setGameModeName(String gameModeName) {
        this.gameModeName = gameModeName;
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

    public static ConfigurationGrid getEmptyGrid() {
        return new ConfigurationGrid();
    }
}
