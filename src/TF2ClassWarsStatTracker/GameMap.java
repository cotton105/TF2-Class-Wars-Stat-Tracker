package TF2ClassWarsStatTracker;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final String mapName;
    private final List<GamemodeGrid> gamemodeGrids;

    public GameMap(String mapName) {
        this.mapName = mapName;
        gamemodeGrids = new ArrayList<>();
    }

    public void addGamemodeGrid(GamemodeGrid grid) {
        gamemodeGrids.add(grid);
    }

    public List<GamemodeGrid> getGamemodeGrids() {
        return gamemodeGrids;
    }

    public String getMapName() {
        return mapName;
    }
}
