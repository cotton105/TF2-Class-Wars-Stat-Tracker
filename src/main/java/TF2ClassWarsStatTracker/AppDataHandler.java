package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.exceptions.GameMapNotFoundException;
import TF2ClassWarsStatTracker.exceptions.MapAlreadyExistsException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;
import TF2ClassWarsStatTracker.gui.tracking.Tracking;
import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.JSONHandler;
import TF2ClassWarsStatTracker.util.Print;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppDataHandler {
    public static final List<GameMap> maps = new ArrayList<>();

    static {
        try {
            List<GameMap> maps = JSONHandler.gameMapsFromJSON();
            AppDataHandler.maps.addAll(maps);
        } catch (FileNotFoundException ex) {
            Print.error(ex.getMessage());
            FileHandler.initialiseJsonMapsFile();
        }
    }

    public static void addMap(String mapName) throws MapAlreadyExistsException {
        if (!mapExists(mapName)) {
            maps.add(new GameMap(mapName));
            Collections.sort(maps);
        } else {
            Print.error(String.format("Map with name \"%s\" already exists", mapName));
            throw new MapAlreadyExistsException(mapName);
        }
    }

    private static boolean mapExists(String mapName) {
        for (GameMap map : maps)
            if (map.getMapName().equals(mapName))
                return true;
        return false;
    }

    public static List<GameModeGrid> getGameModeGrids(String mapName) throws GameMapNotFoundException {
        return getMap(mapName).getGameModeGrids();
    }

    public static int getTotalGames(String mapName, int gameMode) throws GameMapNotFoundException {
        int games = 0;
        if (mapName.equals(Tracking.OVERALL_MAP) && gameMode == -1)
            for (GameMap map : getMaps())
                for (GameModeGrid grid : map.getGameModeGrids())
                    for (int[][] bluMercenary : grid.getMatchupWins())
                        for (int[] redMercenary : bluMercenary)
                            for (int winCount : redMercenary)
                                games += winCount;
        else if (gameMode == -1)
            for (GameModeGrid grid : getGameModeGrids(mapName))
                for (int[][] bluMercenary : grid.getMatchupWins())
                    for (int[] redMercenary : bluMercenary)
                        for (int winCount : redMercenary)
                            games += winCount;
        else if (mapName.equals(Tracking.OVERALL_MAP))
            for (GameMap map : getMaps())
                for (int[][] bluMercenary : map.getGameModeGrid(gameMode).getMatchupWins())
                    for (int[] redMercenary : bluMercenary)
                        for (int winCount : redMercenary)
                            games += winCount;
        else
            for (int[][] bluMercenary : getMap(mapName).getGameModeGrid(gameMode).getMatchupWins())
                for (int[] redMercenary : bluMercenary)
                    for (int winCount : redMercenary)
                        games += winCount;
        return games;
    }

    public static GameMap getMap(String mapName) throws GameMapNotFoundException {
        for (GameMap map : maps) {
            if (map.getMapName().equals(mapName))
                return map;
        }
        throw new GameMapNotFoundException(mapName);
    }

    public static List<GameMap> getMaps() {
        return maps;
    }

    public static void incrementWins(String mapName, int gameMode, int bluMercenary, int redMercenary, int team) throws GameMapNotFoundException, IndexOutOfBoundsException {
        getMap(mapName).incrementWins(gameMode, team, bluMercenary, redMercenary);
    }

    public static int[] getMatchupWins(String mapName, int gameMode, int bluMercenary, int redMercenary) throws GameMapNotFoundException {
        int[] wins = new int[2];
        if (mapName.equals(Tracking.OVERALL_MAP) && gameMode == -1)
            for (GameMap map : getMaps())
                for (GameModeGrid grid : map.getGameModeGrids()) {
                    int[] matchupWins = grid.getMatchupWins(bluMercenary, redMercenary);
                    for (int i=0; i<matchupWins.length; i++)
                        wins[i] += matchupWins[i];
                }
        else if (gameMode == -1)
            for (GameModeGrid grid : getGameModeGrids(mapName)) {
                int[] matchupWins = grid.getMatchupWins(bluMercenary, redMercenary);
                for (int i=0; i<matchupWins.length; i++)
                    wins[i] += matchupWins[i];
            }
        else if (mapName.equals(Tracking.OVERALL_MAP))
            for (GameMap map : getMaps()) {
                int[] matchupWins = map.getGameModeGrid(gameMode).getMatchupWins(bluMercenary, redMercenary);
                for (int i=0; i<matchupWins.length; i++)
                    wins[i] += matchupWins[i];
            }
        else
            wins = getMap(mapName).getGameModeGrid(gameMode).getMatchupWins(bluMercenary, redMercenary);
        return wins;
    }

    public static GameModeGrid getOverallGrid() {
        int[][][] mercenaryWins = new int[9][9][2];
        for (GameMap map : getMaps()) {
            List<GameModeGrid> grids = map.getGameModeGrids();
            for (GameModeGrid grid : grids)
                addMercenaryWinsFromGrid(mercenaryWins, grid.getMatchupWins());
        }
        return new GameModeGrid(mercenaryWins);
    }

    public static GameModeGrid getOverallGrid(String mapName) throws GameMapNotFoundException {
        int[][][] mercenaryWins = new int[9][9][2];
        GameMap map = getMap(mapName);
        for (GameModeGrid grid : map.getGameModeGrids())
            addMercenaryWinsFromGrid(mercenaryWins, grid.getMatchupWins());
        return new GameModeGrid(mercenaryWins);
    }

    public static GameModeGrid getGameModeOverallGrid(int gameMode) {
        int[][][] mercenaryWins = new int[9][9][2];
        for (GameMap map : getMaps()) {
            GameModeGrid grid = map.getGameModeGrid(gameMode);
            addMercenaryWinsFromGrid(mercenaryWins, grid.getMatchupWins());
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
