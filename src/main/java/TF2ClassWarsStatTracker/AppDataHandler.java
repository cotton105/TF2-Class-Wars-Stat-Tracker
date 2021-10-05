package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.exceptions.ActionHistoryEmptyException;
import TF2ClassWarsStatTracker.exceptions.MapNotFoundException;
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
    public static final String NEW_MAP = "newmap", RECORD_WIN = "recordwin";
    public static final List<GameMap> MAPS;
    private static final List<String> ACTION_HISTORY;
    private static final int ACTION_HISTORY_MAX_LENGTH = 10;

    static {
        MAPS = new ArrayList<>();
        ACTION_HISTORY = new ArrayList<>();
        try {
            List<GameMap> maps = JSONHandler.gameMapsFromJSON();
            AppDataHandler.MAPS.addAll(maps);
        } catch (FileNotFoundException ex) {
            Print.error(ex.getMessage());
            FileHandler.initialiseJsonMapsFile();
        }
    }

    public static void addMap(String mapName) throws MapAlreadyExistsException {
        if (!mapExists(mapName)) {
            MAPS.add(new GameMap(mapName));
            Collections.sort(MAPS);
        } else {
            Print.error(String.format("Map with name \"%s\" already exists", mapName));
            throw new MapAlreadyExistsException(mapName);
        }
    }

    public static void removeMap(String mapName) throws MapNotFoundException {
        if (mapExists(mapName)) {
            MAPS.remove(getMap(mapName));
        } else {
            Print.formatError("Map with name \"%s\" does not exist", mapName);
            throw new MapNotFoundException(mapName);
        }
    }

    private static boolean mapExists(String mapName) {
        for (GameMap map : MAPS)
            if (map.getMapName().equals(mapName))
                return true;
        return false;
    }

    public static List<GameModeGrid> getGameModeGrids(String mapName) throws MapNotFoundException {
        return getMap(mapName).getGameModeGrids();
    }

    public static int getTotalGames(String mapName, int gameMode) throws MapNotFoundException {
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

    public static GameMap getMap(String mapName) throws MapNotFoundException {
        for (GameMap map : MAPS) {
            if (map.getMapName().equals(mapName))
                return map;
        }
        throw new MapNotFoundException(mapName);
    }

    public static List<GameMap> getMaps() {
        return MAPS;
    }

    public static void incrementWins(String mapName, int gameMode, int bluMercenary, int redMercenary, int team) throws MapNotFoundException, IndexOutOfBoundsException {
        getMap(mapName).incrementWins(gameMode, bluMercenary, redMercenary, team);
    }

    private static void decrementWins(String mapName, int gameMode, int bluMercenary, int redMercenary, int team) throws MapNotFoundException {
        getMap(mapName).decrementWins(gameMode, bluMercenary, redMercenary, team);
    }

    public static int[] getMatchupWins(String mapName, int gameMode, int bluMercenary, int redMercenary) throws MapNotFoundException {
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

    public static GameModeGrid getOverallGrid(String mapName) throws MapNotFoundException {
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

    public static void updateActionHistory(String action) {
        ACTION_HISTORY.add(action);
        if (ACTION_HISTORY.size() > ACTION_HISTORY_MAX_LENGTH)
            ACTION_HISTORY.remove(0);
    }

    public static void undoLastAction() throws MapNotFoundException, ActionHistoryEmptyException {
        if (!ACTION_HISTORY.isEmpty()) {
            String lastAction = getLastAction();
            String[] lastActionSplit = lastAction.split("-");
            switch (lastActionSplit[0]) {
                case NEW_MAP -> {
                    String mapName = lastActionSplit[1];
                    removeMap(mapName);
                }
                case AppDataHandler.RECORD_WIN -> {
                    String mapName = lastActionSplit[1];
                    int gameMode = Integer.parseInt(lastActionSplit[2]);
                    int bluMercenary = Integer.parseInt(lastActionSplit[3]);
                    int redMercenary = Integer.parseInt(lastActionSplit[4]);
                    int team = Integer.parseInt(lastActionSplit[5]);
                    decrementWins(mapName, gameMode, bluMercenary, redMercenary, team);
                }
            }
            removeLastAction();
        } else
            throw new ActionHistoryEmptyException("No actions to undo");
    }

    private static void removeLastAction() {
        ACTION_HISTORY.remove(ACTION_HISTORY.size()-1);
    }

    private static String getLastAction() {
        return ACTION_HISTORY.get(ACTION_HISTORY.size()-1);
    }
}
