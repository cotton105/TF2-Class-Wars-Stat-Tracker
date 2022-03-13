package TF2ClassWarsStatTracker.game;

import java.util.ArrayList;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.GAME_MODES;

public class LegacyGameMap implements Comparable<LegacyGameMap> {
    private final List<ConfigurationGrid> gameModeGrids;
    private String mapName;

    public LegacyGameMap(String mapName) {
        this.mapName = mapName;
        gameModeGrids = new ArrayList<>();
        initialiseGameModeGrids();
    }

    public void initialiseGameModeGrids() {
        for (int i=0; i<GAME_MODES.length; i++)
            gameModeGrids.add(new ConfigurationGrid());
    }

    public List<ConfigurationGrid> getGameModeGrids() {
        return gameModeGrids;
    }

    public ConfigurationGrid getGameModeGrid(int gameMode) {
        return gameModeGrids.get(gameMode);
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public int compareTo(LegacyGameMap gameMap) {
        return getMapName().compareTo(gameMap.getMapName());
    }
}
