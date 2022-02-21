package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.AppDataHandler;
import TF2ClassWarsStatTracker.exceptions.InvalidTeamNumberException;
import TF2ClassWarsStatTracker.exceptions.NoMatchingRecordException;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.GameModeGrid;

import java.sql.*;
import java.util.List;

import static TF2ClassWarsStatTracker.util.Constants.BLU;
import static TF2ClassWarsStatTracker.util.Constants.RED;

public class DBHandler {
    private static final String PROGRAM_DIR = System.getProperty("user.dir");
    private static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ClassWarsMatchups",
                    "root",
                    ":U5)0y,8!i1r2{oEk@B6D7`O+t9C=zZm"
            );

//                FileHandler.createDirectory(PROGRAM_DIR + "/db");
//                String dbStorageDir = "jdbc:sqlite:" + PROGRAM_DIR + "/db/matchupdata.db";
//                conn = DriverManager.getConnection(dbStorageDir);
                if (conn != null) {
                    DatabaseMetaData meta = conn.getMetaData();
                    System.out.println("The driver name is " + meta.getDriverName());
                    System.out.println("A database connection has been made.");
                }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static boolean matchupExists(String mapName, int gameMode) {
//        PreparedStatement matchupExistsStmt = conn.prepareStatement(
//                "SELECT mtch.MatchupID " +
//                    "FROM Matchup mtch " +
//                    "JOIN StageGameMode sgm ON sgm.ConfigurationID = " +
//                    "JOIN Map mp ON " +
//                    "WHERE mp.MapName = ? AND g.GameModeName = ?"
//        )
        return false;
    }

    private static int[] getMatchupScores(String mapName, int stageNum, int gameMode, int bluMercenary, int redMercenary) throws SQLException, NoMatchingRecordException {
        int[] scores = new int[2];
        PreparedStatement getCurrentWinsStmt = conn.prepareStatement(
                "SELECT mtch.BluWins, mtch.RedWins FROM Matchup mtch " +
                        "JOIN StageGameMode sgm ON sgm.ConfigurationID = mtch.ConfigurationID " +
                        "JOIN Stage s ON s.StageID = sgm.StageID " +
                        "JOIN Map mp ON mp.MapID = s.MapID " +
                        "JOIN GameMode g ON g.GameModeID = sgm.GameModeID " +
                        "WHERE mp.MapName = ? AND s.StageNumber = ? AND g.GameModeName = ? AND mtch.BluMercenaryID = ? AND mtch.RedMercenaryID = ?"
        );
        getCurrentWinsStmt.setString(1, mapName);
        getCurrentWinsStmt.setInt(2, stageNum);
        getCurrentWinsStmt.setString(3, Constants.GAME_MODES[gameMode + 1]);
        getCurrentWinsStmt.setInt(4, bluMercenary + 1);
        getCurrentWinsStmt.setInt(5, redMercenary + 1);

        ResultSet getCurrentWinsRS = getCurrentWinsStmt.executeQuery();
        if (!getCurrentWinsRS.next()) {
            throw new NoMatchingRecordException(
                    String.format("Record matching parameters mapName = %s, stageNum = %d, gameMode = %d, " +
                            "bluMercenary = %d, redMercenary = %d was not found."
                    , mapName, stageNum, gameMode, bluMercenary, redMercenary));
        }
        scores[BLU] = getCurrentWinsRS.getInt(1);
        scores[RED] = getCurrentWinsRS.getInt(2);
        return scores;
    }

    // TODO: https://stackoverflow.com/a/30569923/18134637
    private static void incrementMatchups(String mapName, int stageNum, int gameMode, int bluMercenary, int redMercenary, int team) throws SQLException, InvalidTeamNumberException {
        if (team != BLU && team != RED) throw new InvalidTeamNumberException();
        try {
            int[] scores = getMatchupScores(mapName, stageNum, gameMode, bluMercenary, redMercenary);
            PreparedStatement incrementMatchupStmt = conn.prepareStatement(String.format(
                    "UPDATE Matchup mtch " +
                            "JOIN StageGameMode sgm ON sgm.ConfigurationID = mtch.ConfigurationID " +
                            "JOIN Stage s ON s.StageID = sgm.StageID " +
                            "JOIN Map mp ON mp.MapID = s.MapID " +
                            "JOIN GameMode g ON g.GameModeID = sgm.GameModeID " +
                            "SET mtch.%s = ? " +
                            "WHERE mp.MapName = ? AND s.StageNumber = ? AND g.GameModeName = ? AND mtch.BluMercenaryID = ? AND mtch.RedMercenaryID = ?"
                    , team == BLU ? "BluWins" : "RedWins")
            );
            incrementMatchupStmt.setInt(1, scores[team] + 1);
            incrementMatchupStmt.setString(2, mapName);
            incrementMatchupStmt.setInt(3, stageNum);
            incrementMatchupStmt.setString(4, Constants.GAME_MODES[gameMode + 1]);
            incrementMatchupStmt.setInt(5, bluMercenary + 1);
            incrementMatchupStmt.setInt(6, redMercenary + 1);
            incrementMatchupStmt.executeUpdate();
        }
        // TODO: Update this so that all the relevant records are present before attempting to add a new matchup entry
        catch (NoMatchingRecordException ex) {
            System.out.println(ex.getMessage());
            PreparedStatement insertMatchupStmt = conn.prepareStatement(
                    "INSERT INTO Matchup(ConfigurationID, BluMercenaryID, RedMercenaryID, BluWins, RedWins) VALUES " +
                            "(" +
                            "(" +
                            "SELECT ConfigurationID " +
                            "FROM StageGameMode sgm " +
                            "JOIN Stage s ON s.StageID = sgm.StageID " +
                            "JOIN Map m ON m.MapID = s.MapID " +
                            "JOIN GameMode g ON g.GameModeID = sgm.GameModeID " +
                            "WHERE m.MapName = ? AND s.StageNumber = ? AND g.GameModeName = ? " +
                            "), " +
                            "?, " +
                            "?, " +
                            "?, " +
                            "? " +
                            ")"
            );
            insertMatchupStmt.setString(1, mapName);
            insertMatchupStmt.setInt(2, stageNum);
            insertMatchupStmt.setString(3, Constants.GAME_MODES[gameMode + 1]);

            insertMatchupStmt.setInt(4, bluMercenary + 1);
            insertMatchupStmt.setInt(5, redMercenary + 1);
            insertMatchupStmt.setInt(6, team == BLU ? 1 : 0);
            insertMatchupStmt.setInt(7, team == BLU ? 0 : 1);
            insertMatchupStmt.executeUpdate();
        }
    }

    private static void testQuery() throws SQLException {
        PreparedStatement statement;
        statement = conn.prepareStatement(
                "SELECT * FROM Matchup"
        );
        ResultSet result = statement.executeQuery();
        System.out.printf("%-20s %-20s %-8s %-8s %-8s %-8s\n",
                "Matchup ID", "Configuration ID", "BLU Merc", "RED Merc", "BLU Wins", "RED Wins");
        while (result.next()) {
            System.out.printf("%-20d %-20d %-8d %-8d %-8d %-8d\n",
                    result.getInt(1),
                    result.getInt(2),
                    result.getInt(3),
                    result.getInt(4),
                    result.getInt(5),
                    result.getInt(6));
        }
    }

    private static void testQuery1() throws SQLException {
        PreparedStatement statement = conn.prepareStatement(
                "SELECT * FROM Objective"
        );
        ResultSet result = statement.executeQuery();
        System.out.printf("%-16s %-32s %-16s\n",
                "Objective ID", "Objective Name", "Objective Prefix");
        while (result.next()) {
            System.out.printf(
                    "%-16d %-32s %-16s\n",
                    result.getInt(1),
                    result.getString(2),
                    result.getString(3)
            );
        }
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
//            insertTest();
//            testQuery();
//            testQuery1();
            LegacyConversion.convertLegacyJSONToDB();
//            incrementMatchups("cp_alloy_rc3", 1, 0, 0, 0, RED);
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
