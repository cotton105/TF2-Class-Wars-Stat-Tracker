package TF2ClassWarsStatTracker.game;

import java.util.ArrayList;
import java.util.List;

public class GameMap implements Comparable<GameMap> {
    private final String mapName;
    private final List<GameModeGrid> gameModeGrids;

    public GameMap(String mapName) {
        this.mapName = mapName;
        gameModeGrids = new ArrayList<>();
        initialiseGameModeGrids();
    }

    public void initialiseGameModeGrids() {
        for (int i=0; i<GameModeGrid.GAME_MODES.length; i++)
            gameModeGrids.add(new GameModeGrid());
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

    public GameModeGrid getGameModeGrid(int gameMode) {
        return gameModeGrids.get(gameMode);
    }

    public String getMapName() {
        return mapName;
    }

    public void incrementWins(int gameMode, int bluMercenary, int redMercenary, int team) throws IndexOutOfBoundsException {
        gameModeGrids.get(gameMode).incrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    public void decrementWins(int gameMode, int bluMercenary, int redMercenary, int team) throws IndexOutOfBoundsException {
        gameModeGrids.get(gameMode).decrementMercenaryWins(team, bluMercenary, redMercenary);
    }

    public void setWins(int gameMode, int bluMercenary, int redMercenary, int[] wins) {
        gameModeGrids.get(gameMode).setMercenaryWins(bluMercenary, redMercenary, wins[0], wins[1]);
    }

    public void setWins(int gameMode, int bluMercenary, int redMercenary, int bluWins, int redWins) {
        gameModeGrids.get(gameMode).setMercenaryWins(bluMercenary, redMercenary, bluWins, redWins);
    }

    @Override
    public int compareTo(GameMap gameMap) {
        return getMapName().compareTo(gameMap.getMapName());
    }

    public static boolean validMapName(String mapName) {
        return (mapName != null && !mapName.equals("") && !mapName.contains(" "));
    }
}
