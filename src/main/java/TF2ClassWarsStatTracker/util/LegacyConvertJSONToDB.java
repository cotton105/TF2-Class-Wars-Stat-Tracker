package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.game.ConfigurationGrid;
import TF2ClassWarsStatTracker.game.LegacyGameMap;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.BLU;
import static TF2ClassWarsStatTracker.util.Constants.RED;

public class LegacyConvertJSONToDB {
    private static final String PROGRAM_DIR = System.getProperty("user.dir");
    private static Connection conn;

    static {
        try {
            FileHandler.createDirectory(PROGRAM_DIR + "\\db\\SQLite");
            String dbStorageDir = "jdbc:sqlite:" + PROGRAM_DIR + "\\db\\SQLite\\ClassWarsMatchups.db";
            conn = DriverManager.getConnection(dbStorageDir);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A database connection has been made.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

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
            } else {
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
        } else {
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

    private static void convert() throws SQLException, FileNotFoundException {
        List<LegacyGameMap> maps = JSONHandler.gameMapsFromJSON();
        String[] JSONGameModes = new String[] {
                "Normal", "Global Rolls", "Madness", "Multiply Weapons' Stats", "Good Rolls"};
        for (LegacyGameMap map : maps) {
            String mapName = map.getMapName();
            insertMapIfMissing(mapName);
            System.out.println(mapName);
            for (int i = 0; i < 5; i++) {
                insertGameModeIfMissing(JSONGameModes[i]);
                insertStageGameModeEntry(mapName, JSONGameModes[i]);
                ConfigurationGrid grid = map.getGameModeGrid(i);
                System.out.println(" | " + JSONGameModes[i]);
                int[][][] scores = grid.getMatchupWins();
                insertMatchupData(mapName, JSONGameModes[i], scores);
            }
        }
    }

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        LegacyConvertJSONToDB.convert();
    }
}
