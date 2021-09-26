package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.util.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) throws IOException {
//        FileHandler.readJSONArray("/maps.json");
        List<GameMap> maps = GameMap.gameMapsFromJSON();
        for (GameMap map : maps) {
            System.out.println(map.getMapName());
        }
    }
}
