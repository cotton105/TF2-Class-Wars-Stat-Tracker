package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.exceptions.InvalidTeamNumberException;
import TF2ClassWarsStatTracker.exceptions.TooManyResultsException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;

import java.sql.*;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.BLU;
import static TF2ClassWarsStatTracker.util.Constants.RED;

public class DBHandler {
    private static final String
            MERCENARY_TABLE = "Mercenary",
            GAMEMODE_TABLE = "GameMode",
            OBJECTIVE_TABLE = "Objective",
            MAP_TABLE = "Map",
            STAGE_TABLE = "Stage",
            STAGEGAMEMODE_TABLE = "StageGameMode",
            MATCHUP_TABLE = "Matchup",
            LEGACYMATCHUP_TABLE = "LegacyMatchup";
    private static final String PROGRAM_DIR = System.getProperty("user.dir");
    private static Connection conn;
    private static PreparedStatement GET_LAST_INSERT_ID_STMT;

    static {
        try {
            FileHandler.createDirectory(PROGRAM_DIR + "\\db\\SQLite");
            String dbStorageDir = "jdbc:sqlite:" + PROGRAM_DIR + "\\db\\SQLite\\ClassWarsMatchups.db";
            conn = DriverManager.getConnection(dbStorageDir);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A database connection has been made.");
                GET_LAST_INSERT_ID_STMT = conn.prepareStatement("SELECT LAST_INSERT_ROWID()");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void incrementMatchups(String mapName, int stageNum, String gameMode, String bluMercenary, String redMercenary, int team) throws InvalidTeamNumberException, SQLException {
        if (team != BLU && team != RED) throw new InvalidTeamNumberException();
        int matchupID = initiateMatchup(mapName, stageNum, gameMode, bluMercenary, redMercenary);
        String winsColumn = team == BLU ? "BluWins" : "RedWins";
        PreparedStatement incrementMatchupStmt = conn.prepareStatement(String.format(
                "UPDATE Matchup SET %s = %s + 1 WHERE MatchupID = ?"
                , winsColumn, winsColumn));
        incrementMatchupStmt.setInt(1, matchupID);
        incrementMatchupStmt.executeUpdate();
        Print.format("Successfully recorded win:\n" +
                "BLU | %-8s %s\n" +
                "RED | %-8s %s"
        , bluMercenary.toUpperCase(), team == BLU ? "+ 1" : "", redMercenary.toUpperCase(), team == RED ? "+ 1" : "");
    }

    private static void decrementMatchups(String mapName, int stageNum, String gameMode, String bluMercenary, String redMercenary, int team) throws SQLException {
        if (team != BLU && team != RED) throw new InvalidTeamNumberException();
        int matchupID = initiateMatchup(mapName, stageNum, gameMode, bluMercenary, redMercenary);
        String winsColumn = team == BLU ? "BluWins" : "RedWins";
        PreparedStatement decrementMatchupStmt = conn.prepareStatement(String.format(
                "UPDATE Matchup SET %s = %s - 1 WHERE MatchupID = ?"
                , winsColumn, winsColumn));
        decrementMatchupStmt.setInt(1, matchupID);
        decrementMatchupStmt.executeUpdate();
    }

    private static int getMercenaryDBID(String mercenaryName) throws SQLException {
        PreparedStatement getMercenaryDBIDStmt = conn.prepareStatement(
                "SELECT COUNT(*), MercenaryID FROM Mercenary WHERE UPPER(MercenaryName) = UPPER(?)"
        );
        getMercenaryDBIDStmt.setString(1, mercenaryName);
        ResultSet getMercenaryDBIDRS = getMercenaryDBIDStmt.executeQuery();
        getMercenaryDBIDRS.next();
        int rowCount = getMercenaryDBIDRS.getInt(1);
        if (rowCount != 1) throw new SQLException("Missing mercenary record from Mercenary table.");
        else {
            return getMercenaryDBIDRS.getInt(2);
        }
    }

    private static int getLastInsertID() throws SQLException {
        ResultSet lastIdRS = GET_LAST_INSERT_ID_STMT.executeQuery();
        lastIdRS.next();
        return lastIdRS.getInt(1);
    }

    private static int initiateMap(String mapName) throws SQLException {
        int mapID;
        PreparedStatement getMapStmt = conn.prepareStatement(
                "SELECT COUNT(*), MapID FROM Map WHERE MapName = ?"
        );
        getMapStmt.setString(1, mapName);
        ResultSet getMapRS = getMapStmt.executeQuery();
        getMapRS.next();
        int rowCount = getMapRS.getInt(1);
        if (rowCount > 1) throw new TooManyResultsException();
        else if (rowCount == 0) {
            PreparedStatement insertMapStmt = conn.prepareStatement(
                    "INSERT INTO Map (MapName, ObjectiveID) VALUES " +
                            "( " +
                            "?, " +
                            "(SELECT ObjectiveID FROM Objective WHERE ObjectivePrefix = ?) " +
                            ")"
            );
            insertMapStmt.setString(1, mapName);
            insertMapStmt.setString(2, mapName.split("_")[0]);  // Get the prefix (gamemode) of the mapName
            insertMapStmt.executeUpdate();
            mapID = getLastInsertID();
            System.out.printf("Missing map \"%s\" added to database (ID=%d).\n", mapName, mapID);
        }
        else {
            mapID = getMapRS.getInt(2);
        }
        return mapID;
    }

    private static int initiateStage(int mapID, int stageNum) throws SQLException {
        int stageID;
        PreparedStatement getStageStmt = conn.prepareStatement(
                "SELECT COUNT(*), StageID FROM Stage WHERE MapID = ? AND StageNumber = ?"
        );
        getStageStmt.setInt(1, mapID);
        getStageStmt.setInt(2, stageNum);
        ResultSet getStageRS = getStageStmt.executeQuery();
        getStageRS.next();
        int rowCount = getStageRS.getInt(1);
        if (rowCount > 1) throw new TooManyResultsException();
        else if (rowCount == 0) {
            PreparedStatement insertStageStmt = conn.prepareStatement(
                    "INSERT INTO Stage(MapID, StageNumber) VALUES " +
                            "(?, ?)"
            );
            insertStageStmt.setInt(1, mapID);
            insertStageStmt.setInt(2, stageNum);
            insertStageStmt.executeUpdate();
            stageID = getLastInsertID();
            System.out.printf("Missing stage \"MapID %d stage %d\" added to database (ID=%d).\n", mapID, stageNum, stageID);
        }
        else {
            stageID = getStageRS.getInt(2);
        }
        return stageID;
    }

    private static int initiateGameMode(String gameMode) throws SQLException {
        int gameModeID;
        PreparedStatement getGameModeStmt = conn.prepareStatement(
                "SELECT COUNT(*), GameModeID FROM GameMode WHERE GameModeName = ?"
        );
        getGameModeStmt.setString(1, gameMode);
        ResultSet getGameModeRS = getGameModeStmt.executeQuery();
        getGameModeRS.next();
        int rowCount = getGameModeRS.getInt(1);
        if (rowCount > 1) throw new TooManyResultsException();
        else if (rowCount == 0) {
            PreparedStatement insertGameModeStmt = conn.prepareStatement(
                    "INSERT INTO GameMode(GameModeName) VALUES (?)"
            );
            insertGameModeStmt.setString(1, gameMode);
            insertGameModeStmt.executeUpdate();
            gameModeID = getLastInsertID();
            System.out.printf("Missing game mode \"%s\" added to database (ID=%d).\n", gameMode, gameModeID);
        }
        else {
            gameModeID = getGameModeRS.getInt(2);
        }
        return gameModeID;
    }

    private static int initiateStageGameMode(int stageID, int gameModeID) throws SQLException {
        int configurationID;
        PreparedStatement getStageGameModeStmt = conn.prepareStatement(
                "SELECT COUNT(*), ConfigurationID FROM StageGameMode WHERE StageID = ? AND GameModeID = ?"
        );
        getStageGameModeStmt.setInt(1, stageID);
        getStageGameModeStmt.setInt(2, gameModeID);
        ResultSet getStageGameModeRS = getStageGameModeStmt.executeQuery();
        getStageGameModeRS.next();
        int rowCount = getStageGameModeRS.getInt(1);
        if (rowCount > 1) throw new TooManyResultsException();
        else if (rowCount == 0) {
            PreparedStatement insertStageGameModeStmt = conn.prepareStatement(
                    "INSERT INTO StageGameMode(StageID, GameModeID) VALUES (?, ?)"
            );
            insertStageGameModeStmt.setInt(1, stageID);
            insertStageGameModeStmt.setInt(2, gameModeID);
            insertStageGameModeStmt.executeUpdate();
            configurationID = getLastInsertID();
            System.out.printf("Missing configuration \"StageID %d GameModeID %d\" added to database (ID=%d).\n", stageID, gameModeID, configurationID);
        }
        else {
            configurationID = getStageGameModeRS.getInt(2);
        }
        return configurationID;
    }

    private static int initiateMatchup(String mapName, int stageNum, String gameMode, String bluMercenary, String redMercenary) throws SQLException {
        int matchupID;
        int mapID = initiateMap(mapName);
        int stageID = initiateStage(mapID, stageNum);
        int gameModeID = initiateGameMode(gameMode);
        int configurationID = initiateStageGameMode(stageID, gameModeID);
        int bluMercenaryID = getMercenaryDBID(bluMercenary);
        int redMercenaryID = getMercenaryDBID(redMercenary);
        PreparedStatement getMatchupStmt = conn.prepareStatement(
                "SELECT COUNT(*), MatchupID FROM Matchup WHERE ConfigurationID = ? AND BluMercenaryID = ? AND RedMercenaryID = ?"
        );
        getMatchupStmt.setInt(1, configurationID);
        getMatchupStmt.setInt(2, bluMercenaryID);
        getMatchupStmt.setInt(3, redMercenaryID);
        ResultSet getMatchupRS = getMatchupStmt.executeQuery();
        getMatchupRS.next();
        int rowCount = getMatchupRS.getInt(1);
        if (rowCount > 1) throw new TooManyResultsException();
        else if (rowCount == 0) {
            PreparedStatement insertMatchupStmt = conn.prepareStatement(
                    "INSERT INTO Matchup(ConfigurationID, BluMercenaryID, RedMercenaryID) VALUES " +
                            "(?, ?, ?)"
            );
            insertMatchupStmt.setInt(1, configurationID);
            insertMatchupStmt.setInt(2, bluMercenaryID);
            insertMatchupStmt.setInt(3, redMercenaryID);
            insertMatchupStmt.executeUpdate();
            matchupID = getLastInsertID();
            System.out.printf("Missing matchup \"ConfigurationID %d BLU %s vs. RED %s\" added to database (ID=%d).\n", configurationID, bluMercenary, redMercenary, matchupID);
        }
        else {
            matchupID = getMatchupRS.getInt(2);
        }
        return matchupID;
    }

    static class LegacyConversion {

        private static void insertMapIfMissing(String mapName) throws SQLException {
            PreparedStatement checkMapExistsStmt = conn.prepareStatement("SELECT MapName FROM Map WHERE MapName = ?");
            checkMapExistsStmt.setString(1, mapName);
            ResultSet checkMapExistsRS = checkMapExistsStmt.executeQuery();
            if (!checkMapExistsRS.next()) {
                String prefix = mapName.split("_")[0];
                PreparedStatement addMapStmt = conn.prepareStatement(
                        "INSERT INTO Map(ObjectiveID, MapName) VALUES" +
                                "(" +
                                "(SELECT ObjectiveID FROM Objective WHERE ObjectivePrefix = ?)," +
                                "?" +
                                ")");
                addMapStmt.setString(1, prefix);
                addMapStmt.setString(2, mapName);
                addMapStmt.executeUpdate();
                if (mapName.contains("dustbowl") || mapName.contains("goldrush")) {
                    for (int i = 1; i <= 3; i++) {
                        PreparedStatement addStageStmt = conn.prepareStatement(
                                "INSERT INTO Stage(MapID, StageNumber) VALUES " +
                                        "( " +
                                        "(SELECT MapID FROM Map WHERE MapName = ?), " +
                                        "? " +
                                        ")"
                        );
                        addStageStmt.setString(1, mapName);
                        addStageStmt.setInt(2, i);
                        addStageStmt.executeUpdate();
                    }
                }
                else {
                    PreparedStatement addStageStmt = conn.prepareStatement(
                            "INSERT INTO Stage(MapID, StageNumber) VALUES " +
                                    "( " +
                                    "(SELECT MapID FROM Map WHERE MapName = ?), " +
                                    "? " +
                                    ")"
                    );
                    addStageStmt.setString(1, mapName);
                    addStageStmt.setInt(2, 1);
                    addStageStmt.executeUpdate();
                }
            }
        }

        private static void insertGameModeIfMissing(String gameModeName) throws SQLException {
            PreparedStatement checkGameModeExistsStmt = conn.prepareStatement(
                    "SELECT GameModeName FROM GameMode WHERE GameModeName = ?"
            );
            checkGameModeExistsStmt.setString(1, gameModeName);
            ResultSet checkGameModeExistsRS = checkGameModeExistsStmt.executeQuery();
            if (!checkGameModeExistsRS.next()) {
                PreparedStatement insertGameModeStmt = conn.prepareStatement(
                        "INSERT INTO GameMode(GameModeName) VALUES" +
                                "(?)"
                );
                insertGameModeStmt.setString(1, gameModeName);
                insertGameModeStmt.executeUpdate();
            }
        }

        private static void insertStageGameModeEntry(String mapName, String gameModeName) throws SQLException {
            PreparedStatement getMapStagesStmt = conn.prepareStatement(
                    "SELECT StageID FROM Stage WHERE MapID = (SELECT MapID FROM Map WHERE MapName = ?)"
            );
            getMapStagesStmt.setString(1, mapName);
            ResultSet getMapStagesRS = getMapStagesStmt.executeQuery();
            while (getMapStagesRS.next()) {
                int stageID = getMapStagesRS.getInt(1);
                PreparedStatement insertStageGameModeStmt = conn.prepareStatement(
                        "INSERT INTO StageGameMode(StageID, GameModeID) VALUES" +
                                "(" +
                                "?," +
                                "(SELECT GameModeID FROM GameMode WHERE GameModeName = ?)" +
                                ")"
                );
                insertStageGameModeStmt.setInt(1, stageID);
                insertStageGameModeStmt.setString(2, gameModeName);
                insertStageGameModeStmt.executeUpdate();
            }
        }

        private static void insertMatchupData(String mapName, String gameModeName, int[][][] scores) throws SQLException {
            if (mapName.contains("dustbowl") || mapName.contains("goldrush")) {
                for (int bluIndex = 0; bluIndex < scores.length; bluIndex++) {
                    for (int redIndex = 0; redIndex < scores[0].length; redIndex++) {
                        PreparedStatement insertMatchupDataStmt = conn.prepareStatement(
                                "INSERT INTO LegacyMatchup(MapID, GameModeID, BluMercenaryID, RedMercenaryID, BluWins, RedWins) VALUES " +
                                        "( " +
                                        "(SELECT MapID FROM Map WHERE MapName = ?), " +
                                        "(SELECT GameModeID FROM GameMode WHERE GameModeName = ?), " +
                                        "(SELECT MercenaryID FROM Mercenary WHERE MercenaryName = ?), " +
                                        "(SELECT MercenaryID FROM Mercenary WHERE MercenaryName = ?), " +
                                        "?, " +
                                        "? " +
                                        ")"
                        );
                        insertMatchupDataStmt.setString(1, mapName);
                        insertMatchupDataStmt.setString(2, gameModeName);
                        insertMatchupDataStmt.setString(3, Constants.MERCENARY[bluIndex]);
                        insertMatchupDataStmt.setString(4, Constants.MERCENARY[redIndex]);
                        insertMatchupDataStmt.setInt(5, scores[bluIndex][redIndex][BLU]);
                        insertMatchupDataStmt.setInt(6, scores[bluIndex][redIndex][RED]);
                        insertMatchupDataStmt.executeUpdate();
                    }
                }
            }
            else {
                for (int bluIndex = 0; bluIndex < scores.length; bluIndex++) {
                    for (int redIndex = 0; redIndex < scores[0].length; redIndex++) {
                        PreparedStatement insertMatchupDataStmt = conn.prepareStatement(
                                "INSERT INTO Matchup(ConfigurationID, BluMercenaryID, RedMercenaryID, BluWins, RedWins) VALUES " +
                                        "( " +
                                        "( " +
                                        "SELECT sgm.ConfigurationID FROM StageGameMode sgm " +
                                        "JOIN Stage s ON s.StageID = sgm.StageID " +
                                        "JOIN Map m ON m.MapID = s.MapID " +
                                        "JOIN GameMode g ON g.GameModeID = sgm.GameModeID " +
                                        "WHERE m.MapName = ? AND g.GameModeName = ? " +
                                        "), " +
                                        "(SELECT MercenaryID FROM Mercenary WHERE MercenaryName = ?), " +
                                        "(SELECT MercenaryID FROM Mercenary WHERE MercenaryName = ?), " +
                                        "?, " +
                                        "? " +
                                        ")"
                        );
                        insertMatchupDataStmt.setString(1, mapName);
                        insertMatchupDataStmt.setString(2, gameModeName);
                        insertMatchupDataStmt.setString(3, Constants.MERCENARY[bluIndex]);
                        insertMatchupDataStmt.setString(4, Constants.MERCENARY[redIndex]);
                        insertMatchupDataStmt.setInt(5, scores[bluIndex][redIndex][BLU]);
                        insertMatchupDataStmt.setInt(6, scores[bluIndex][redIndex][RED]);
                        insertMatchupDataStmt.executeUpdate();
                    }
                }
            }
        }

        private static void convertLegacyJSONToDB() throws SQLException {
            List<GameMap> maps = AppDataHandler.getMaps();
            for (GameMap map : maps) {
                String mapName = map.getMapName();
                insertMapIfMissing(mapName);
                System.out.println(mapName);
                for (int i = 0; i < 5; i++) {
                    insertGameModeIfMissing(Constants.GAME_MODES[i + 1]);
                    insertStageGameModeEntry(mapName, Constants.GAME_MODES[i + 1]);
                    GameModeGrid grid = map.getGameModeGrid(i);
                    System.out.println(" | " + Constants.GAME_MODES[i + 1]);
                    int[][][] scores = grid.getMatchupWins();
                    insertMatchupData(mapName, Constants.GAME_MODES[i + 1], scores);
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
//            LegacyConversion.convertLegacyJSONToDB();
            incrementMatchups("koth_increment_test", 2, Constants.GAME_MODES[2], "SPY", "HEAVY", RED);
//            incrementMatchups("koth_test_map3", 1, Constants.GAME_MODES[5], "Heavy", "Spy", BLU);
//            decrementMatchups("koth_increment_test", 2, Constants.GAME_MODES[2], "SPY", "HEAVY", RED);
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
