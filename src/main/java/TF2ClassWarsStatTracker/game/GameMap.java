package TF2ClassWarsStatTracker.game;

import java.util.ArrayList;
import java.util.List;

public class GameMap implements Comparable<GameMap> {
    private final List<GameStage> stages;
    private String mapName;

    public GameMap(int stageCount) {
        this.mapName = null;
        stages = new ArrayList<>();
        for (int i = 0; i < stageCount; i++) {
            stages.add(new GameStage());
        }
//        initialiseGameModeGrids();
    }

    public GameMap(String mapName) {
        this.mapName = mapName;
        stages = new ArrayList<>();
//        initialiseGameModeGrids();
    }

    public void addStage() {
        stages.add(new GameStage());
    }

    public void initialiseGameModeGrids() {
//        for (int i=0; i<GAME_MODES.length; i++)
//            gameModeGrids.add(new ConfigurationGrid());
    }

    public void addGameModeGrid(ConfigurationGrid grid) {
//        gameModeGrids.add(grid);
    }

    public void addGameModeGrids(ArrayList<ConfigurationGrid> grids) {
//        gameModeGrids.addAll(grids);
    }

    public List<ConfigurationGrid> getGameModeGrids() {
//        return gameModeGrids;
        return null;
    }

    public ConfigurationGrid getGameModeGrid(int gameMode) {
//        return gameModeGrids.get(gameMode);
        return null;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void incrementWins(int gameMode, int bluMercenary, int redMercenary, int team) throws IndexOutOfBoundsException {
//        gameModeGrids.get(gameMode).incrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    public void decrementWins(int gameMode, int bluMercenary, int redMercenary, int team) throws IndexOutOfBoundsException {
//        gameModeGrids.get(gameMode).decrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    @Override
    public int compareTo(GameMap gameMap) {
        return getMapName().compareTo(gameMap.getMapName());
    }

    public static boolean validMapName(String mapName) {
        return (mapName != null && !mapName.equals("") && !mapName.contains(" "));
    }
}
