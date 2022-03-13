package TF2ClassWarsStatTracker;

import TF2ClassWarsStatTracker.exceptions.*;
import TF2ClassWarsStatTracker.game.*;
import TF2ClassWarsStatTracker.util.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.*;

public class AppDataHandler {
    public static final String NEW_MAP = "newmap";
    public static final String RECORD_WIN = "recordwin";
    static List<LegacyGameMap> MAPS;
    static List<String> GAME_MODES;
    private static ConfigurationGrid loadedConfiguration;
    private static final List<String> ACTION_HISTORY;
    private static final int ACTION_HISTORY_MAX_LENGTH = 10;

    static {
        MAPS = new ArrayList<>();
        GAME_MODES = new ArrayList<>();
        ACTION_HISTORY = new ArrayList<>();
        try {
            loadedConfiguration = DBHandler.Retrieve.getConfiguration(1);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Print.error(ex.getMessage());
            FileHandler.initialiseJsonMapsFile();
        }
    }

    public static void setLoadedConfiguration(ConfigurationGrid grid) {
        loadedConfiguration = grid;
    }

    public static List<String> getMapNames() throws SQLException {
        return DBHandler.Retrieve.getMapNames();
    }

    public static void incrementLoadedGridWins(int team, int bluMercenary, int redMercenary) {
        try {
            loadedConfiguration.incrementMercenaryWins(team, bluMercenary, redMercenary);
            DBHandler.Update.incrementWins(team, MERCENARY[bluMercenary], MERCENARY[redMercenary], loadedConfiguration);
        } catch (InvalidConfigurationGridException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void decrementLoadedGridWins(int team, int bluMercenary, int redMercenary) {
        try {
            loadedConfiguration.decrementMercenaryWins(team, bluMercenary, redMercenary);
            DBHandler.Update.decrementWins(team, MERCENARY[bluMercenary], MERCENARY[redMercenary], loadedConfiguration);
        } catch (InvalidConfigurationGridException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void decrementWins(int team, int bluMercenary, int redMercenary, String mapName, int stageNumber, int gameMode) throws MapNotFoundException {
//        getMap(mapName).decrementWins(gameMode, bluMercenary, redMercenary, team);
    }

//    public static float[][] getBroadMercenaryAverages(String mapName, int gameMode) throws MapNotFoundException {
//        float[][] broadMercenaryAverages = new float[3][9];
//        LegacyGameMap map = getMap(mapName);
//        ConfigurationGrid grid = map.getGameModeGrid(gameMode);
//        for (int i = 0; i < 9; i++) {
//            float averageBlu = 0f;
//            float averageRed = 0f;
//            for (int j = 0; j < 9; j++) {
//                int[] matchupWinsBluSide = grid.getMatchupWins(i, j);
//                int[] matchupWinsRedSide = grid.getMatchupWins(j, i);
//                averageBlu += Calculate.getRatioBias(matchupWinsBluSide[BLU], matchupWinsBluSide[RED]);
//                averageRed += Calculate.getRatioBias(matchupWinsRedSide[BLU], matchupWinsBluSide[RED]);
//            }
//            broadMercenaryAverages[BLU][i] = averageBlu;
//            broadMercenaryAverages[RED][i] = averageRed;
//            broadMercenaryAverages[2][i] = (averageBlu + averageRed) / 2;
//        }
//        return broadMercenaryAverages;
//    }

    public static void updateActionHistory(String action) {
        ACTION_HISTORY.add(action);
        if (ACTION_HISTORY.size() > ACTION_HISTORY_MAX_LENGTH)
            ACTION_HISTORY.remove(0);
    }

    public static void undoLastAction() throws Exception {
        if (!ACTION_HISTORY.isEmpty()) {
            String lastAction = getLastAction();
            String[] lastActionSplit = lastAction.split("-");
            switch (lastActionSplit[0]) {
                case NEW_MAP -> {
                    String mapName = lastActionSplit[1];
                    DBHandler.Update.removeMap(mapName);
                }
                case AppDataHandler.RECORD_WIN -> {
                    String mapName = lastActionSplit[1];
                    int gameMode = Integer.parseInt(lastActionSplit[2]);
                    int bluMercenary = Integer.parseInt(lastActionSplit[3]);
                    int redMercenary = Integer.parseInt(lastActionSplit[4]);
                    int team = Integer.parseInt(lastActionSplit[5]);
                    // TODO: fix undo
                    decrementWins(team, bluMercenary, redMercenary, mapName, 1, gameMode);
                }
            }
            removeLastAction();
        } else
            throw new ActionHistoryEmptyException("No actions to undo");
    }

    private static void removeLastAction() {
        ACTION_HISTORY.remove(ACTION_HISTORY.size()-1);
    }

    private static String getLastAction() {
        return ACTION_HISTORY.get(ACTION_HISTORY.size()-1);
    }
}
