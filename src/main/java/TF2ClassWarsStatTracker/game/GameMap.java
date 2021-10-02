package TF2ClassWarsStatTracker.game;

import TF2ClassWarsStatTracker.exceptions.GameMapNotFoundException;
import TF2ClassWarsStatTracker.gui.tracking.Tracking;
import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.JSONHandler;
import TF2ClassWarsStatTracker.util.Print;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final String mapName;
    private final List<GameModeGrid> gameModeGrids;

    public static final List<GameMap> maps = new ArrayList<>();

    static {
        try {
            List<GameMap> maps = JSONHandler.gameMapsFromJSON();
            GameMap.maps.addAll(maps);
        } catch (FileNotFoundException ex) {
            Print.error(ex.getMessage());
            FileHandler.initialiseJsonMapsFile();
        }
    }

    public GameMap(String mapName) {
        this.mapName = mapName;
        gameModeGrids = new ArrayList<>();
        initialiseGameModeGrids();
    }

    public void initialiseGameModeGrids() {
        for (int i=0; i<GameModeGrid.GAME_MODES.length; i++)
            gameModeGrids.add(new GameModeGrid());
    }

    public void addGameModeGrid(GameModeGrid grid) {
        gameModeGrids.add(grid);
    }

    public void addGameModeGrids(ArrayList<GameModeGrid> grids) {
        gameModeGrids.addAll(grids);
    }

    public List<GameModeGrid> getGameModeGrids() {
        return gameModeGrids;
    }

    public GameModeGrid getGameModeGrid(int gameMode) {
        return gameModeGrids.get(gameMode);
    }

    public String getMapName() {
        return mapName;
    }

    public void incrementWins(int gameMode, int team, int bluMercenary, int redMercenary) throws IndexOutOfBoundsException {
        gameModeGrids.get(gameMode).incrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    public void setWins(int gameMode, int bluMercenary, int redMercenary, int[] wins) {
        gameModeGrids.get(gameMode).setMercenaryWins(bluMercenary, redMercenary, wins[0], wins[1]);
    }

    public void setWins(int gameMode, int bluMercenary, int redMercenary, int bluWins, int redWins) {
        gameModeGrids.get(gameMode).setMercenaryWins(bluMercenary, redMercenary, bluWins, redWins);
    }

    public static void incrementWins(String mapName, int gameMode, int bluMercenary, int redMercenary, int team) throws GameMapNotFoundException, IndexOutOfBoundsException {
        getMap(mapName).incrementWins(gameMode, team, bluMercenary, redMercenary);
    }

    private static List<GameModeGrid> getGameModeGrids(String mapName) throws GameMapNotFoundException {
        return getMap(mapName).getGameModeGrids();
    }

    public static int[] getMatchupWins(String mapName, int gameMode, int bluMercenary, int redMercenary) throws GameMapNotFoundException {
        int[] wins = new int[2];
        if (mapName.equals(Tracking.OVERALL_MAP) && gameMode == -1)
            for (GameMap map : GameMap.getMaps())
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
            for (GameMap map : GameMap.getMaps()) {
                int[] matchupWins = map.getGameModeGrid(gameMode).getMatchupWins(bluMercenary, redMercenary);
                for (int i=0; i<matchupWins.length; i++)
                    wins[i] += matchupWins[i];
            }
        else
            wins = GameMap.getMap(mapName).getGameModeGrid(gameMode).getMatchupWins(bluMercenary, redMercenary);
        return wins;
    }

    public static int getTotalGames(String mapName, int gameMode) throws GameMapNotFoundException {
        int games = 0;
        if (mapName.equals(Tracking.OVERALL_MAP) && gameMode == -1)
            for (GameMap map : GameMap.getMaps())
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
            for (GameMap map : GameMap.getMaps())
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
}
