package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.game.GameMap;

import java.util.List;

public class test {
    public static void main(String[] args) {
        List<GameMap> maps = GameMap.gameMapsFromJSON();
        for (GameMap map : maps) {
            System.out.println(map.getMapName());
        }
    }
}
