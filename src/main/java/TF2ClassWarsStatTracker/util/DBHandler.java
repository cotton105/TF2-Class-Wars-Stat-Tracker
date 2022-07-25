package TF2ClassWarsStatTracker.util;

import TF2ClassWarsStatTracker.exceptions.*;
import TF2ClassWarsStatTracker.game.GameMap;
import TF2ClassWarsStatTracker.game.ConfigurationGrid;
import org.apache.commons.lang3.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static TF2ClassWarsStatTracker.util.Constants.*;
import static TF2ClassWarsStatTracker.util.DBHandler.Retrieve.*;

public class DBHandler {
    private static final String PROGRAM_DIR = System.getProperty("user.dir");
    private static Map<String, Integer> MERCENARY_ID;
    private static PreparedStatement getLastInsertIdStmt;
    private static Connection conn;

    static {
        try {
            String dbStorageDir = PROGRAM_DIR + "/db/SQLite/ClassWarsMatchups.db";
            setSQLiteConnection(dbStorageDir);
            MERCENARY_ID = Retrieve.getMercenaryIDMap();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static class Retrieve {

        private Retrieve() {
            throw new IllegalStateException();
        }

        private static Map<String, Integer> getMercenaryIDMap() throws SQLException {
            Map<String, Integer> ids = new HashMap<>();
            String query = "SELECT MercenaryName, MercenaryID FROM Mercenary";
            try (ResultSet getMercenaryIDsRS = conn.createStatement().executeQuery(query)) {
                while (getMercenaryIDsRS.next()) {
                    ids.put(getMercenaryIDsRS.getString(1), getMercenaryIDsRS.getInt(2));
                }
            }
            return ids;
        }

        public static int[] getMatchupWins(String bluMercenary, String redMercenary) {
            int[] wins = new int[2];
            String query = "SELECT SUM(mtch.BluWins), SUM(mtch.RedWins) " +
                    "FROM Matchup mtch " +
                    "JOIN Mercenary blu ON blu.MercenaryID = mtch.BluMercenaryID " +
                    "JOIN Mercenary red ON red.MercenaryID = mtch.RedMercenaryID " +
                    "WHERE blu.MercenaryName = ? AND red.MercenaryName = ?";
            try (PreparedStatement getMatchupWinsStmt = conn.prepareStatement(query)) {
                getMatchupWinsStmt.setString(1, bluMercenary);
                getMatchupWinsStmt.setString(2, redMercenary);
                ResultSet getMatchupWinsRS = getMatchupWinsStmt.executeQuery();
                getMatchupWinsRS.next();
                wins[BLU] = getMatchupWinsRS.getInt(1);
                wins[RED] = getMatchupWinsRS.getInt(2);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return wins;
        }

        public static int[] getMatchupWins(String bluMercenary, String redMercenary, String mapName, int stageNumber) throws SQLException {
            if (countMatchingMaps(mapName) == 0) throw new MapNotFoundException(mapName);
            int[] wins = new int[2];
            String query = "SELECT SUM(mtch.BluWins), SUM(mtch.RedWins) " +
                    "FROM Matchup mtch " +
                    "JOIN Mercenary blu ON blu.MercenaryID = mtch.BluMercenaryID " +
                    "JOIN Mercenary red ON red.MercenaryID = mtch.RedMercenaryID " +
                    "JOIN StageGameMode sgm ON sgm.ConfigurationID = mtch.ConfigurationID " +
                    "JOIN Stage s ON s.StageID = sgm.StageID " +
                    "JOIN Map mp ON mp.MapID = s.MapID " +
                    "WHERE blu.MercenaryName = ? AND red.MercenaryName = ? AND mp.MapName = ? AND s.StageNumber = ?";
            try (PreparedStatement getMatchupWinsStmt = conn.prepareStatement(query)) {
                getMatchupWinsStmt.setString(1, bluMercenary);
                getMatchupWinsStmt.setString(2, redMercenary);
                getMatchupWinsStmt.setString(3, mapName);
                getMatchupWinsStmt.setInt(4, stageNumber);
                ResultSet getMatchupWinsRS = getMatchupWinsStmt.executeQuery();
                getMatchupWinsRS.next();
                wins[BLU] = getMatchupWinsRS.getInt(1);
                wins[RED] = getMatchupWinsRS.getInt(2);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return wins;
        }

        public static int[] getMatchupWins(String bluMercenary, String redMercenary, String gameModeName) {
            int[] wins = new int[2];
            String query = "SELECT SUM(mtch.BluWins), SUM(mtch.RedWins) " +
                    "FROM Matchup mtch " +
                    "JOIN Mercenary blu ON blu.MercenaryID = mtch.BluMercenaryID " +
                    "JOIN Mercenary red ON red.MercenaryID = mtch.RedMercenaryID " +
                    "JOIN StageGameMode sgm ON sgm.ConfigurationID = mtch.ConfigurationID " +
                    "JOIN GameMode g ON g.GameModeID = sgm.GameModeID " +
                    "WHERE blu.MercenaryName = ? AND red.MercenaryName = ? AND g.GameModeName = ?";
            try (PreparedStatement getMatchupWinsStmt = conn.prepareStatement(query)) {
                getMatchupWinsStmt.setString(1, bluMercenary);
                getMatchupWinsStmt.setString(2, redMercenary);
                getMatchupWinsStmt.setString(3, gameModeName);
                ResultSet getMatchupWinsRS = getMatchupWinsStmt.executeQuery();
                getMatchupWinsRS.next();
                wins[BLU] = getMatchupWinsRS.getInt(1);
                wins[RED] = getMatchupWinsRS.getInt(2);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return wins;
        }

        public static int[] getMatchupWins(String bluMercenary, String redMercenary, String mapName, int stageNumber, String gameModeName) throws SQLException {
            if (countMatchingMaps(mapName) == 0) throw new MapNotFoundException(mapName);
            int[] wins = new int[2];
            String query =
                    "SELECT SUM(mtch.BluWins), SUM(mtch.RedWins) " +
                        "FROM Matchup mtch " +
                            "JOIN Mercenary blu ON blu.MercenaryID = mtch.BluMercenaryID " +
                            "JOIN Mercenary red ON red.MercenaryID = mtch.RedMercenaryID " +
                            "JOIN StageGameMode sgm ON sgm.ConfigurationID = mtch.ConfigurationID " +
                            "JOIN Stage s ON s.StageID = sgm.StageID " +
                            "JOIN Map mp ON mp.MapID = s.MapID " +
                            "JOIN GameMode g ON g.GameModeID = sgm.GameModeID " +
                        "WHERE blu.MercenaryName = ? AND red.MercenaryName = ? AND mp.MapName = ? AND s.StageNumber = ? AND g.GameModeName = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, bluMercenary);
                statement.setString(2, redMercenary);
                statement.setString(3, mapName);
                statement.setInt(4, stageNumber);
                statement.setString(5, gameModeName);
                ResultSet resultSet = statement.executeQuery();
                resultSet.next();
                wins[BLU] = resultSet.getInt(1);
                wins[RED] = resultSet.getInt(2);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return wins;
        }

        static Integer getMercenaryDBID(String mercenaryName) {
            String query = "SELECT COUNT(*), MercenaryID FROM Mercenary WHERE UPPER(MercenaryName) = UPPER(?)";
            try (PreparedStatement getMercenaryDBIDStmt = conn.prepareStatement(query)) {
                getMercenaryDBIDStmt.setString(1, mercenaryName);
                ResultSet getMercenaryDBIDRS = getMercenaryDBIDStmt.executeQuery();
                getMercenaryDBIDRS.next();
                int rowCount = getMercenaryDBIDRS.getInt(1);
                if (rowCount != 1) throw new SQLException("Missing mercenary record from Mercenary table.");
                else {
                    return getMercenaryDBIDRS.getInt(2);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        static int getLastInsertID() throws SQLException {
            ResultSet lastIdRS = getLastInsertIdStmt.executeQuery();
            lastIdRS.next();
            return lastIdRS.getInt(1);
        }

        public static ConfigurationGrid getConfiguration(int configurationID) throws SQLException {
            ConfigurationGrid configurationGrid = new ConfigurationGrid();
            String query = "SELECT BluMercenaryID, RedMercenaryID, BluWins, RedWins FROM Matchup WHERE ConfigurationID = ?";
            try (PreparedStatement getConfigurationStmt = conn.prepareStatement(query)) {
                getConfigurationStmt.setInt(1, configurationID);
                ResultSet getConfigurationRS = getConfigurationStmt.executeQuery();
                while (getConfigurationRS.next()) {
                    int bluMercenary = getConfigurationRS.getInt(1) - 1;
                    int redMercenary = getConfigurationRS.getInt(2) - 1;
                    int bluWins = getConfigurationRS.getInt(3);
                    int redWins = getConfigurationRS.getInt(4);
                    configurationGrid.setMercenaryWins(bluMercenary, redMercenary, bluWins, redWins);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return configurationGrid;
        }

        public static int countMatchingMaps(String mapName) throws SQLException {
            String query = "SELECT COUNT(*) FROM Map WHERE MapName = ?";
            try (PreparedStatement checkMapExistsStmt = conn.prepareStatement(query)) {
                checkMapExistsStmt.setString(1, mapName);
                ResultSet checkMapExistsRS = checkMapExistsStmt.executeQuery();
                checkMapExistsRS.next();
                return checkMapExistsRS.getInt(1);
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        public static int getMapID(String mapName) throws SQLException {
            String query = "SELECT MapID, COUNT(*) FROM Map WHERE MapName = ?";
            try (PreparedStatement getMapIdStmt = conn.prepareStatement(query)) {
                getMapIdStmt.setString(1, mapName);
                ResultSet getMapIdRS = getMapIdStmt.executeQuery();
                if (getMapIdRS.next()) {
                    if (getMapIdRS.getInt(2) == 1)
                        return getMapIdRS.getInt(1);
                    else
                        throw new TooManyResultsException(mapName);
                } else {
                    throw new MapNotFoundException(mapName);
                }
            }
        }

        public static String getFirstAlphabeticalMapName() throws SQLException {
            String query = "SELECT MapName FROM Map ORDER BY MapName ASC LIMIT 1";
            try (ResultSet getFirstMapRS = conn.createStatement().executeQuery(query)) {
                getFirstMapRS.next();
                return getFirstMapRS.getString(1);
            }
        }

        public static ConfigurationGrid getOverallGrid() {
            ConfigurationGrid grid = new ConfigurationGrid();
            for (int bluMercenary = 0; bluMercenary < MERCENARY.length; bluMercenary++) {
                for (int redMercenary = 0; redMercenary < MERCENARY.length; redMercenary++) {
                    int[] wins = getMatchupWins(MERCENARY[bluMercenary], MERCENARY[redMercenary]);
                    grid.setMercenaryWins(bluMercenary, redMercenary, wins[BLU], wins[RED]);
                }
            }
            return grid;
        }

        public static ConfigurationGrid getOverallGrid(String mapName, int stageNumber) throws SQLException {
            ConfigurationGrid grid = new ConfigurationGrid();
            grid.setMapName(mapName);
            grid.setStageNumber(stageNumber);
            for (int bluMercenary = 0; bluMercenary < MERCENARY.length; bluMercenary++) {
                for (int redMercenary = 0; redMercenary < MERCENARY.length; redMercenary++) {
                    int[] wins = getMatchupWins(MERCENARY[bluMercenary], MERCENARY[redMercenary], mapName, stageNumber);
                    grid.setMercenaryWins(bluMercenary, redMercenary, wins[BLU], wins[RED]);
                }
            }
            return grid;
        }

        public static ConfigurationGrid getOverallGameModeGrid(String gameModeName) {
            ConfigurationGrid grid = new ConfigurationGrid();
            grid.setGameModeName(gameModeName);
            for (int bluMercenary = 0; bluMercenary < MERCENARY.length; bluMercenary++) {
                for (int redMercenary = 0; redMercenary < MERCENARY.length; redMercenary++) {
                    int[] wins = getMatchupWins(MERCENARY[bluMercenary], MERCENARY[redMercenary], gameModeName);
                    grid.setMercenaryWins(bluMercenary, redMercenary, wins[BLU], wins[RED]);
                }
            }
            return grid;
        }

        public static ConfigurationGrid getMatchupGrid(String mapName, int stageNumber, String gameModeName) throws SQLException {
            ConfigurationGrid grid = new ConfigurationGrid();
            grid.setMapName(mapName);
            grid.setStageNumber(stageNumber);
            grid.setGameModeName(gameModeName);
            for (int bluMercenary = 0; bluMercenary < MERCENARY.length; bluMercenary++) {
                for (int redMercenary = 0; redMercenary < MERCENARY.length; redMercenary++) {
                    int[] wins = getMatchupWins(MERCENARY[bluMercenary], MERCENARY[redMercenary], mapName, stageNumber, gameModeName);
                    grid.setMercenaryWins(bluMercenary, redMercenary, wins[BLU], wins[RED]);
                }
            }
            return grid;
        }

        public static int getTotalGameCount() throws SQLException {
            String query = "SELECT SUM(BluWins) + SUM(RedWins) " +
                    "+ (SELECT SUM(BluWins) + SUM(RedWins) FROM LegacyMatchup) " +
                    "FROM Matchup";
            try (PreparedStatement getTotalGameCountStmt = conn.prepareStatement(query)) {
                ResultSet getTotalGameCountRS = getTotalGameCountStmt.executeQuery();
                getTotalGameCountRS.next();
                return getTotalGameCountRS.getInt(1);
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        public static int getTotalGameCount(String mapName, int stageNumber, String gameModeName) throws SQLException {
            try {
                int mapID = getMapID(mapName);
                int stageID = getStageID(mapID, stageNumber);
                int gameModeID = getGameModeID(gameModeName);
                String query = "SELECT SUM(m.BluWins) + SUM(m.RedWins) " +
                        "FROM Matchup m " +
                        "JOIN StageGameMode sgm ON sgm.ConfigurationID = m.ConfigurationID " +
                        "WHERE sgm.StageID = ? AND sgm.GameModeID = ?";
                try (PreparedStatement getTotalGameCountStmt = conn.prepareStatement(query)) {
                    getTotalGameCountStmt.setInt(1, stageID);
                    getTotalGameCountStmt.setInt(2, gameModeID);
                    ResultSet getTotalGameCountRS = getTotalGameCountStmt.executeQuery();
                    getTotalGameCountRS.next();
                    return getTotalGameCountRS.getInt(1);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        public static int getTotalGameCount(String mapName, int stageNumber) throws SQLException {
            try {
                int mapID = getMapID(mapName);
                int stageID = getStageID(mapID, stageNumber);
                String query = "SELECT SUM(m.BluWins) + SUM(m.RedWins) " +
                        "FROM Matchup m " +
                        "JOIN StageGameMode sgm ON sgm.ConfigurationID = m.ConfigurationID " +
                        "WHERE sgm.StageID = ?";
                try (PreparedStatement getTotalGameCountStmt = conn.prepareStatement(query)) {
                    getTotalGameCountStmt.setInt(1, stageID);
                    ResultSet getTotalGameCountRS = getTotalGameCountStmt.executeQuery();
                    getTotalGameCountRS.next();
                    return getTotalGameCountRS.getInt(1);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        public static int getTotalGameCount(String gameModeName) throws SQLException {
            try {
                int gameModeID = getGameModeID(gameModeName);
                String query = "SELECT SUM(m.BluWins) + SUM(m.RedWins) " +
                        "FROM Matchup m " +
                        "JOIN StageGameMode sgm ON sgm.ConfigurationID = m.ConfigurationID " +
                        "WHERE sgm.GameModeID = ?";
                try (PreparedStatement getTotalGameCountStmt = conn.prepareStatement(query)) {
                    getTotalGameCountStmt.setInt(1, gameModeID);
                    ResultSet getTotalGameCountRS = getTotalGameCountStmt.executeQuery();
                    getTotalGameCountRS.next();
                    return getTotalGameCountRS.getInt(1);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        private static int getGameModeID(String gameModeName) throws SQLException {
            String query = "SELECT GameModeID, COUNT(*) FROM GameMode WHERE GameModeName = ?";
            try (PreparedStatement getGameModeIDStmt = conn.prepareStatement(query)) {
                getGameModeIDStmt.setString(1, gameModeName);
                ResultSet getStageIDRS = getGameModeIDStmt.executeQuery();
                if (getStageIDRS.next()) {
                    if (getStageIDRS.getInt(2) == 1)
                        return getStageIDRS.getInt(1);
                    else
                        throw new TooManyResultsException(query);
                } else {
                    throw new GameModeNotFoundException(gameModeName);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        private static int getStageID(int mapID, int stageNumber) throws SQLException {
            String query = "SELECT StageID, COUNT(*) FROM Stage WHERE MapID = ? AND StageNumber = ?";
            try (PreparedStatement getStageIDStmt = conn.prepareStatement(query)) {
                getStageIDStmt.setInt(1, mapID);
                getStageIDStmt.setInt(2, stageNumber);
                ResultSet getStageIDRS = getStageIDStmt.executeQuery();
                if (getStageIDRS.next()) {
                    if (getStageIDRS.getInt(2) == 1)
                        return getStageIDRS.getInt(1);
                    else
                        throw new TooManyResultsException(query);
                } else {
                    throw new StageNotFoundException(mapID, stageNumber);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw ex;
            }
        }

        public static List<String> getMapNames() throws SQLException {
            List<String> mapNames = new ArrayList<>();
            try (ResultSet mapNamesRS = conn.createStatement().executeQuery("SELECT MapName FROM Map")) {
                while (mapNamesRS.next()) {
                    mapNames.add(mapNamesRS.getString(1));
                }
                return mapNames;
            }
        }

        public static List<String> getGameModeNames() throws SQLException {
            List<String> names = new ArrayList<>();
            String query = "SELECT GameModeName FROM GameMode";
            try (ResultSet getGameModeNamesRS = conn.createStatement().executeQuery(query)) {
                while (getGameModeNamesRS.next()) {
                    names.add(getGameModeNamesRS.getString(1));
                }
                return names;
            }
        }
    }

    public static class Update {

        private Update() {
            throw new IllegalStateException();
        }

        public static void incrementWins(int team, String bluMercenary, String redMercenary, String mapName, int stageNumber, String gameModeName) throws InvalidTeamNumberException, SQLException {
            if (team != BLU && team != RED) throw new InvalidTeamNumberException();
            int matchupID = initiateMatchup(mapName, stageNumber, gameModeName, bluMercenary, redMercenary);
            String winsColumn = team == BLU ? "BluWins" : "RedWins";
            String query = String.format("UPDATE Matchup SET %s = %s + 1 WHERE MatchupID = ?", winsColumn, winsColumn);
            try (PreparedStatement incrementMatchupStmt = conn.prepareStatement(query)) {
                incrementMatchupStmt.setInt(1, matchupID);
                incrementMatchupStmt.executeUpdate();
                Print.format(
                        "Successfully recorded win:\n" +
                                "BLU | %-8s %s\n" +
                                "RED | %-8s %s"
                        , bluMercenary.toUpperCase(), team == BLU ? "+ 1" : "", redMercenary.toUpperCase(), team == RED ? "+ 1" : "");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        public static void decrementWins(int team, String bluMercenary, String redMercenary, String mapName, int stageNumber, String gameModeName) throws SQLException {
            if (team != BLU && team != RED) throw new InvalidTeamNumberException();
            int matchupID = initiateMatchup(mapName, stageNumber, gameModeName, bluMercenary, redMercenary);
            String winsColumn = team == BLU ? "BluWins" : "RedWins";
            String query = String.format("UPDATE Matchup SET %s = %s - 1 WHERE MatchupID = ?", winsColumn, winsColumn);
            try (PreparedStatement decrementMatchupStmt = conn.prepareStatement(query)) {
                decrementMatchupStmt.setInt(1, matchupID);
                decrementMatchupStmt.executeUpdate();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        public static void incrementWins(int team, String bluMercenary, String redMercenary, ConfigurationGrid grid) throws SQLException, InvalidConfigurationGridException {
            String mapName = grid.getMapName();
            Integer stageNumber = grid.getStageNumber();
            String gameModeName = grid.getGameModeName();
            if (mapName == null || stageNumber == null || gameModeName == null) throw new InvalidConfigurationGridException(grid);
            incrementWins(team, bluMercenary, redMercenary, mapName, stageNumber, gameModeName);
        }

        public static void decrementWins(int team, String bluMercenary, String redMercenary, ConfigurationGrid grid) throws SQLException, InvalidConfigurationGridException {
            String mapName = grid.getMapName();
            Integer stageNumber = grid.getStageNumber();
            String gameModeName = grid.getGameModeName();
            if (mapName == null || stageNumber == null || gameModeName == null) throw new InvalidConfigurationGridException(grid);
            decrementWins(team, bluMercenary, redMercenary, mapName, stageNumber, gameModeName);
        }

        private static boolean insertMap(String mapName) {
            String query = "INSERT INTO Map (MapName, ObjectiveID) VALUES (" +
                    "?, (SELECT ObjectiveID FROM Objective WHERE ObjectivePrefix = ?)" +
                    ")";
            try (PreparedStatement insertMapStmt = conn.prepareStatement(query)) {
                insertMapStmt.setString(1, mapName);
                insertMapStmt.setString(2, mapName.split("_")[0]);  // Get the prefix (gamemode) of the mapName
                insertMapStmt.executeUpdate();
                return true;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public static boolean addMap(String mapName) throws InvalidMapNameException, MapAlreadyExistsException {
            if (!GameMap.validMapName(mapName)) throw new InvalidMapNameException(mapName);
            try {
                if (countMatchingMaps(mapName) > 0) throw new MapAlreadyExistsException(mapName);
                return Update.insertMap(mapName);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

        public static void removeMap(String mapName) throws NotImplementedException, SQLException {
            int rowCount = countMatchingMaps(mapName);
            if (rowCount > 1) throw new TooManyResultsException();
            else if (rowCount == 0) throw new MapNotFoundException(mapName);
            else {
                throw new NotImplementedException("Map removal from database is not yet supported.");
            }
        }

        public static boolean renameMap(String mapName, String newName) throws InvalidMapNameException {
            if (!GameMap.validMapName(newName)) throw new InvalidMapNameException(newName);
            try {
                int rowCount = countMatchingMaps(mapName);
                if (rowCount == 0) throw new MapNotFoundException(mapName);
                else if (rowCount > 1) throw new TooManyResultsException(mapName);
                else {
                    String query = "UPDATE Map SET MapName = ? WHERE MapID = ?";
                    try (PreparedStatement renameMapStmt = conn.prepareStatement(query)) {
                        renameMapStmt.setString(1, newName);
                        renameMapStmt.setInt(2, getMapID(mapName));
                        renameMapStmt.executeUpdate();
                        return true;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }

    }

    private static int initiateMap(String mapName) throws SQLException {
        int mapID;
        String query = "SELECT COUNT(*), MapID FROM Map WHERE MapName = ?";
        try (PreparedStatement getMapStmt = conn.prepareStatement(query)) {
            getMapStmt.setString(1, mapName);
            ResultSet getMapRS = getMapStmt.executeQuery();
            getMapRS.next();
            int rowCount = getMapRS.getInt(1);
            if (rowCount > 1) throw new TooManyResultsException();
            else if (rowCount == 0) {
                Update.insertMap(mapName);
                mapID = getLastInsertID();
                Print.format("Missing map \"%s\" added to database (ID=%d).\n", mapName, mapID);
            }
            else {
                mapID = getMapRS.getInt(2);
            }
            return mapID;
        }
    }

    private static int initiateStage(int mapID, int stageNum) throws SQLException {
        int stageID;
        String query = "SELECT COUNT(*), StageID FROM Stage WHERE MapID = ? AND StageNumber = ?";
        try (PreparedStatement getStageStmt = conn.prepareStatement(query)) {
            getStageStmt.setInt(1, mapID);
            getStageStmt.setInt(2, stageNum);
            ResultSet getStageRS = getStageStmt.executeQuery();
            getStageRS.next();
            int rowCount = getStageRS.getInt(1);
            if (rowCount > 1) throw new TooManyResultsException();
            else if (rowCount == 0) {
                query = "INSERT INTO Stage(MapID, StageNumber) VALUES (?, ?)";
                try (PreparedStatement insertStageStmt = conn.prepareStatement(query)) {
                    insertStageStmt.setInt(1, mapID);
                    insertStageStmt.setInt(2, stageNum);
                    insertStageStmt.executeUpdate();
                    stageID = getLastInsertID();
                    Print.format("Missing stage \"MapID %d stage %d\" added to database (ID=%d).\n",
                            mapID, stageNum, stageID);
                }
            }
            else {
                stageID = getStageRS.getInt(2);
            }
            return stageID;
        }
    }

    private static int initiateGameMode(String gameMode) throws SQLException {
        int gameModeID;
        String query = "SELECT COUNT(*), GameModeID FROM GameMode WHERE GameModeName = ?";
        try (PreparedStatement getGameModeStmt = conn.prepareStatement(query)) {
            getGameModeStmt.setString(1, gameMode);
            ResultSet getGameModeRS = getGameModeStmt.executeQuery();
            getGameModeRS.next();
            int rowCount = getGameModeRS.getInt(1);
            if (rowCount > 1) throw new TooManyResultsException();
            else if (rowCount == 0) {
                query = "INSERT INTO GameMode(GameModeName) VALUES (?)";
                try (PreparedStatement insertGameModeStmt = conn.prepareStatement(query)) {
                    insertGameModeStmt.setString(1, gameMode);
                    insertGameModeStmt.executeUpdate();
                    gameModeID = getLastInsertID();
                    Print.format("Missing game mode \"%s\" added to database (ID=%d).\n", gameMode, gameModeID);
                }
            }
            else {
                gameModeID = getGameModeRS.getInt(2);
            }
            return gameModeID;
        }
    }

    private static int initiateStageGameMode(int stageID, int gameModeID) throws SQLException {
        int configurationID;
        String query = "SELECT COUNT(*), ConfigurationID FROM StageGameMode WHERE StageID = ? AND GameModeID = ?";
        try (PreparedStatement getStageGameModeStmt = conn.prepareStatement(query)) {
            getStageGameModeStmt.setInt(1, stageID);
            getStageGameModeStmt.setInt(2, gameModeID);
            ResultSet getStageGameModeRS = getStageGameModeStmt.executeQuery();
            getStageGameModeRS.next();
            int rowCount = getStageGameModeRS.getInt(1);
            if (rowCount > 1) throw new TooManyResultsException();
            else if (rowCount == 0) {
                query = "INSERT INTO StageGameMode(StageID, GameModeID) VALUES (?, ?)";
                try (PreparedStatement insertStageGameModeStmt = conn.prepareStatement(query)) {
                    insertStageGameModeStmt.setInt(1, stageID);
                    insertStageGameModeStmt.setInt(2, gameModeID);
                    insertStageGameModeStmt.executeUpdate();
                    configurationID = getLastInsertID();
                    Print.format("Missing configuration \"StageID %d GameModeID %d\" added to database (ID=%d).\n", stageID, gameModeID, configurationID);
                }
            }
            else {
                configurationID = getStageGameModeRS.getInt(2);
            }
            return configurationID;
        }
    }

    private static int initiateMatchup(String mapName, int stageNum, String gameMode, String bluMercenary, String redMercenary) throws SQLException {
        int matchupID;
        int mapID = initiateMap(mapName);
        int stageID = initiateStage(mapID, stageNum);
        int gameModeID = initiateGameMode(gameMode);
        int configurationID = initiateStageGameMode(stageID, gameModeID);
        int bluMercenaryID = getMercenaryDBID(bluMercenary);
        int redMercenaryID = getMercenaryDBID(redMercenary);
        String query = "SELECT COUNT(*), MatchupID FROM Matchup " +
                "WHERE ConfigurationID = ? AND BluMercenaryID = ? AND RedMercenaryID = ?";
        try (PreparedStatement getMatchupStmt = conn.prepareStatement(query)) {
            getMatchupStmt.setInt(1, configurationID);
            getMatchupStmt.setInt(2, bluMercenaryID);
            getMatchupStmt.setInt(3, redMercenaryID);
            ResultSet getMatchupRS = getMatchupStmt.executeQuery();
            getMatchupRS.next();
            int rowCount = getMatchupRS.getInt(1);
            if (rowCount > 1) throw new TooManyResultsException();
            else if (rowCount == 0) {
                query = "INSERT INTO Matchup(ConfigurationID, BluMercenaryID, RedMercenaryID) VALUES (?, ?, ?)";
                try (PreparedStatement insertMatchupStmt = conn.prepareStatement(query)) {
                    insertMatchupStmt.setInt(1, configurationID);
                    insertMatchupStmt.setInt(2, bluMercenaryID);
                    insertMatchupStmt.setInt(3, redMercenaryID);
                    insertMatchupStmt.executeUpdate();
                    matchupID = getLastInsertID();
                    Print.format("Missing matchup \"ConfigurationID %d BLU %s vs. RED %s\" added to database (ID=%d).\n",
                            configurationID, bluMercenary, redMercenary, matchupID);
                }
            }
            else {
                matchupID = getMatchupRS.getInt(2);
            }
            return matchupID;

        }
    }

    public static void setSQLiteConnection(String dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        DatabaseMetaData meta = conn.getMetaData();
        System.out.println("The driver name is " + meta.getDriverName());
        System.out.println("A database connection has been made.");
        getLastInsertIdStmt = conn.prepareStatement("SELECT LAST_INSERT_ROWID()");
    }
}
