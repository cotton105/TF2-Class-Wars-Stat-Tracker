package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.util.FileHandler;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.Reader;
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

    public static List<GameMap> gameMapsFromJSON() {
        Gson gson = new Gson();
        List<GameMap> maps = new ArrayList<>();
        JsonArray jsonArray = FileHandler.readJSONArray("/maps.json");
        for (JsonElement mapElement : jsonArray) {
            JsonObject jsonMap = mapElement.getAsJsonObject();
            maps.add(gson.fromJson(jsonMap, GameMap.class));
        }
        return maps;
//        jsonObject.addProperty("mapName", );
    }
}
