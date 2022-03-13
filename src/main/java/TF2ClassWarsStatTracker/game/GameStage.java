package TF2ClassWarsStatTracker.game;

import TF2ClassWarsStatTracker.util.DBHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStage {
    private final Map<String, ConfigurationGrid> gameModeGrids;

    public GameStage() {
        gameModeGrids = new HashMap<>();
        List<String> gameModeNames = DBHandler.Retrieve.getGameModeNames();
        for (String name : gameModeNames) {
            gameModeGrids.put(name, new ConfigurationGrid());
        }
    }
}
