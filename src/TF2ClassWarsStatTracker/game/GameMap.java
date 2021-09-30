package TF2ClassWarsStatTracker.game;

import TF2ClassWarsStatTracker.Start;
import TF2ClassWarsStatTracker.exceptions.GameMapNotFoundException;
import TF2ClassWarsStatTracker.util.JSONHandler;

import javax.swing.*;
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
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Start.getMainMenu(), ex.getMessage(), "App load failure", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public GameMap(String mapName) {
        this.mapName = mapName;
        gameModeGrids = new ArrayList<>();
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

    public static void incrementWins(String mapName, int gameMode, int redMercenary, int bluMercenary, int team) throws GameMapNotFoundException, IndexOutOfBoundsException {
        getMap(mapName).incrementWins(gameMode, team, bluMercenary, redMercenary);
    }

    private static List<GameModeGrid> getGameModeGrids(String mapName) throws GameMapNotFoundException {
        return getMap(mapName).getGameModeGrids();
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
