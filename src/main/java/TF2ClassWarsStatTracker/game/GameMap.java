package TF2ClassWarsStatTracker.game;

import java.util.ArrayList;
import java.util.List;

public class GameMap implements Comparable<GameMap> {
    private String mapName;

    public GameMap(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    @Override
    public int compareTo(GameMap gameMap) {
        return getMapName().compareTo(gameMap.getMapName());
    }

    public static boolean validMapName(String mapName) {
        return (mapName != null && !mapName.equals("") && !mapName.contains(" "));
    }
}
