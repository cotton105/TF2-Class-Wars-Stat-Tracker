package TF2ClassWarsStatTracker.game;

import java.util.ArrayList;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.GAME_MODES;

public class GameMap implements Comparable<GameMap> {
    private final List<ConfigurationGrid> configurationGrids;
    private String mapName;

    public GameMap(String mapName) {
        this.mapName = mapName;
        configurationGrids = new ArrayList<>();
        initialiseGameModeGrids();
    }

    public void initialiseGameModeGrids() {
        for (int i=0; i<GAME_MODES.length; i++)
            configurationGrids.add(new ConfigurationGrid());
    }

    public void addGameModeGrid(ConfigurationGrid grid) {
        configurationGrids.add(grid);
    }

    public void addGameModeGrids(ArrayList<ConfigurationGrid> grids) {
        configurationGrids.addAll(grids);
    }

    public List<ConfigurationGrid> getGameModeGrids() {
        return configurationGrids;
    }

    public ConfigurationGrid getGameModeGrid(int gameMode) {
        return configurationGrids.get(gameMode);
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void incrementWins(int gameMode, int bluMercenary, int redMercenary, int team) throws IndexOutOfBoundsException {
        configurationGrids.get(gameMode).incrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    public void decrementWins(int gameMode, int bluMercenary, int redMercenary, int team) throws IndexOutOfBoundsException {
        configurationGrids.get(gameMode).decrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    public void setWins(int gameMode, int bluMercenary, int redMercenary, int[] wins) {
        configurationGrids.get(gameMode).setMercenaryWins(bluMercenary, redMercenary, wins[0], wins[1]);
    }

    public void setWins(int gameMode, int bluMercenary, int redMercenary, int bluWins, int redWins) {
        configurationGrids.get(gameMode).setMercenaryWins(bluMercenary, redMercenary, bluWins, redWins);
    }

    @Override
    public int compareTo(GameMap gameMap) {
        return getMapName().compareTo(gameMap.getMapName());
    }

    public static boolean validMapName(String mapName) {
        return (mapName != null && !mapName.equals("") && !mapName.contains(" "));
    }
}
