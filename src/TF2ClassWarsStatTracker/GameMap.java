package TF2ClassWarsStatTracker;

import java.util.ArrayList;

public class GameMap {
    private ArrayList<GamemodeGrid> gamemodeGrids;

    public GameMap() {
        gamemodeGrids = new ArrayList<>();
    }

    public void addGamemodeGrid(GamemodeGrid grid) {
        gamemodeGrids.add(grid);
    }
}
