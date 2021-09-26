package TF2ClassWarsStatTracker.game;

import TF2ClassWarsStatTracker.util.FileHandler;
import TF2ClassWarsStatTracker.util.Print;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GameMap {
    private final String mapName;
    private final List<GameModeGrid> gameModeGrids;

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

    public String getMapName() {
        return mapName;
    }

    public static List<GameMap> gameMapsFromJSON() {
        Gson gson = new Gson();
        List<GameMap> maps = new ArrayList<>();
        JsonArray jsonArray = FileHandler.readJSONArray(FileHandler.MAPS_JSON);
        for (JsonElement mapElement : jsonArray) {
            JsonObject jsonMap = mapElement.getAsJsonObject();
            maps.add(gson.fromJson(jsonMap, GameMap.class));
        }
        return maps;
    }

    public static GameMap gameMapFromJSON(String mapName) {
        Gson gson = new Gson();
        for (JsonElement mapElement : FileHandler.readJSONArray(FileHandler.MAPS_JSON)) {
            JsonObject jsonMap = mapElement.getAsJsonObject();
            GameMap map = gson.fromJson(jsonMap, GameMap.class);
            if (map.mapName.equals(mapName)) {
//                map.addGameModeGrids(getGameModeGrids(jsonMap));
                return map;
            }
        }
        throw new NullPointerException();
    }

    private static ArrayList<GameModeGrid> getGameModeGrids(JsonObject jsonMap) {
        Gson gson = new Gson();
        JsonArray gridsJSONArray = jsonMap.getAsJsonArray("gameModeGrids");
        ArrayList<GameModeGrid> grids = new ArrayList<>();
        for (JsonElement gridJSONElement : gridsJSONArray) {
            JsonObject gridJSONObject = gridJSONElement.getAsJsonObject();
            grids.add(gson.fromJson(gridJSONObject, GameModeGrid.class));
        }
        return grids;
    }
}
