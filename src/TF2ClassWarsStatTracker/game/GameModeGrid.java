package TF2ClassWarsStatTracker.game;

import java.util.List;

public class GameModeGrid {
    public final static int NORMAL = 0, GLOBAL_ROLLS = 1, MADNESS = 2, MULTIPLY_WEAPONS_STATS = 3, GOOD_ROLLS = 4;
    public final static String[] GAME_MODE = new String[] {
            "Normal", "Global Rolls",
            "Madness", "Multiply Weapons' Stats",
            "Good Rolls"};
    private final int[][][] mercenaryWins;  // [BLU Mercenary] [RED Mercenary] [{BLU Wins, RED Wins}]

    public GameModeGrid() {
        mercenaryWins = new int[9][9][2];
    }

    private GameModeGrid(int[][][] mercenaryWins) {
        this.mercenaryWins = mercenaryWins;
    }

    public void setMercenaryWins(int bluMercenary, int redMercenary, int bluWins, int redWins) {
        mercenaryWins[bluMercenary][redMercenary] = new int[] {bluWins, redWins};
    }

    public void incrementMercenaryWins(int team, int bluMercenary, int redMercenary) {
        mercenaryWins[bluMercenary][redMercenary][team]++;
    }

    public int[][][] getMercenaryWins() {
        return mercenaryWins;
    }

    public static GameModeGrid getOverallGrid() {
        List<GameMap> gameMaps = GameMap.gameMapsFromJSON();
        int[][][] mercenaryWins = new int[9][9][2];
        for (GameMap map : gameMaps) {
            List<GameModeGrid> grids = map.getGameModeGrids();
            for (GameModeGrid grid : grids)
                addMercenaryWinsFromGrid(mercenaryWins, grid.getMercenaryWins());
        }
        return new GameModeGrid(mercenaryWins);
    }

    public static GameModeGrid getOverallGrid(String mapName) {
        int[][][] mercenaryWins = new int[9][9][2];
        GameMap map = GameMap.gameMapFromJSON(mapName);
        for (GameModeGrid grid : map.getGameModeGrids())
            addMercenaryWinsFromGrid(mercenaryWins, grid.getMercenaryWins());
        return new GameModeGrid(mercenaryWins);
    }

    public static GameModeGrid getGameModeOverallGrid(int gameMode) {
        int[][][] mercenaryWins = new int[9][9][2];
        for (GameMap map : GameMap.gameMapsFromJSON()) {
            GameModeGrid grid = map.getGameModeGrid(gameMode);
            addMercenaryWinsFromGrid(mercenaryWins, grid.getMercenaryWins());
        }
        return new GameModeGrid(mercenaryWins);
    }

    private static void addMercenaryWinsFromGrid(int[][][] subjectGrid, int[][][] existingGrid) {
        for (int i=0; i<existingGrid.length; i++)
            for (int j=0; j<existingGrid[0].length; j++)
                for (int k=0; k<existingGrid[0][0].length; k++)
                    subjectGrid[i][j][k] += existingGrid[i][j][k];
    }
}
