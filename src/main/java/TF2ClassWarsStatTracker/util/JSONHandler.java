package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.ConfigurationGrid;
import TF2ClassWarsStatTracker.game.LegacyGameMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JSONHandler {
    private static final String
            MAP_NAME = "mapName",
            GAMEMODE_GRIDS = "gameModeGrids",
            MERCENARY_WINS = "mercenaryWins";

    public static GameMap getGameMapFromCSV(String filename) throws IOException {
        GameMap map = new GameMap(FileHandler.removeFileExtension(filename));
        List<String[]> lines = FileHandler.readCSVLines(filename);
        int intsStartIndex = getIntegerStartIndex(lines);
        for (int gamemode=0; gamemode<5; gamemode++) {
            ConfigurationGrid grid = new ConfigurationGrid();
            for (int row=0; row<9; row++) {
                int[] lineTrimmed = trimLineToInts(lines.get(intsStartIndex+(row*2)));
                int[] nextLineTrimmed = trimLineToInts(lines.get(intsStartIndex+(row*2)+1));
                for (int column=0; column<9; column++) {
                    int bluWins = lineTrimmed[column + (9 * gamemode)]-1;
                    int redWins = nextLineTrimmed[column + (9 * gamemode)]-1;
                    grid.setMercenaryWins(column, row, bluWins, redWins);
                }
            }
            map.addGameModeGrid(grid);
        }
        return map;
    }

    private static JsonArray getMatchupArr(JsonArray mapsArr, String mapName, int gameMode, int bluMercenary, int redMercenary) {
        int index = getKeyIndex(mapName, MAP_NAME, mapsArr, true);
        JsonObject mapObj = mapsArr.get(index).getAsJsonObject();
        JsonObject gridObj = mapObj.get(GAMEMODE_GRIDS).getAsJsonArray().get(gameMode).getAsJsonObject();
        return gridObj.get(MERCENARY_WINS).getAsJsonArray()
                .get(bluMercenary).getAsJsonArray()
                .get(redMercenary).getAsJsonArray();
    }

    private static int[] trimLineToInts(String[] line) {
        ArrayList<Integer> lineArrayListTrimmed = new ArrayList<>();
        for (String item : line) {
            if (item.matches("-?\\d+"))
                lineArrayListTrimmed.add(Integer.parseInt(item));
        }
        int[] lineArrayTrimmed = new int[lineArrayListTrimmed.size()];
        for (int i=0; i<lineArrayListTrimmed.size(); i++)
            lineArrayTrimmed[i] = lineArrayListTrimmed.get(i);
        return lineArrayTrimmed;
    }

    // Find the line in the .csv that has the integer values
    private static int getIntegerStartIndex(List<String[]> lines) {
        for (int line=0; line<lines.size(); line++) {
            String[] lineStrArray = lines.get(line);
            for (String s : lineStrArray) {
                String itemStr = String.valueOf(s);
                if (itemStr.matches("-?\\d+")) {
                    return line;
                }
            }
        }
        return -1;
    }

    private static int getKeyIndex(String value, String key, JsonArray arr) {
        for (int i=0; i<arr.size(); i++)
            if (arr.get(i).getAsJsonObject().get(key).getAsString().equals(value))
                return i;
        return -1;
    }

    /**
     * Binary search for the given {@code value}.
     * @param value search term
     * @param key JSON key name to match
     * @param arr where to search for the value
     * @param sorted whether {@code arr} is sorted (for Binary Search)
     * @return index of the found value
     */
    private static int getKeyIndex(String value, String key, JsonArray arr, boolean sorted) {
        if (sorted) {
            int pointer = arr.size()/2;
            int remainingSize = arr.size() - pointer;
            while (true) {
                String pointerValue = arr.get(pointer).getAsJsonObject().get(key).getAsString();
                if (pointerValue.equals(value))
                    return pointer;
                else if (value.compareTo(pointerValue) < 0) {
                    remainingSize = remainingSize/2;
                    if (pointer % 2 == 1) pointer -= 1;
                    pointer -= remainingSize;
                } else {
                    remainingSize = remainingSize/2;
                    if (pointer % 2 == 1) pointer += 1;
                    pointer += remainingSize;
                }
            }
        } else
            return getKeyIndex(value, key, arr);
    }

    public static void mapCSVsToJSON() throws IOException {
        List<File> fileList = FileHandler.getFilesInDirectory(new File("res/maps"));
        List<GameMap> maps = new ArrayList<>();
        for (File file : fileList) {
            GameMap map = JSONHandler.getGameMapFromCSV(file.getName());
            maps.add(map);
        }
        FileHandler.writeToJSONFile(maps, FileHandler.DEFAULT_MAPS_JSON);
    }

    public static List<LegacyGameMap> gameMapsFromJSON() throws FileNotFoundException {
        Gson gson = new Gson();
        List<LegacyGameMap> maps = new ArrayList<>();
        JsonArray jsonArray = FileHandler.readJSONArray(FileHandler.DEFAULT_MAPS_JSON);
        for (JsonElement mapElement : jsonArray) {
            JsonObject jsonMap = mapElement.getAsJsonObject();
            maps.add(gson.fromJson(jsonMap, LegacyGameMap.class));
        }
        return maps;
    }

    public static GameMap gameMapFromJSON(String mapName) throws FileNotFoundException {
        Gson gson = new Gson();
        for (JsonElement mapElement : FileHandler.readJSONArray(FileHandler.DEFAULT_MAPS_JSON)) {
            JsonObject jsonMap = mapElement.getAsJsonObject();
            GameMap map = gson.fromJson(jsonMap, GameMap.class);
            if (map.getMapName().equals(mapName))
                return map;
        }
        throw new FileNotFoundException();
    }
}
