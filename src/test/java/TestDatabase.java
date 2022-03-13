import TF2ClassWarsStatTracker.util.DBHandler;
import org.junit.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDatabase {
    Connection conn;

    @Before
    public void createDatabaseConnection() throws SQLException {
        String cwd = System.getProperty("user.dir");
        String testDbLocation = "jdbc:sqlite:" + cwd + "\\src\\test\\res\\TestClassWarsMatchups.db";
        String realDbLocation = "jdbc:sqlite:" + cwd + "\\db\\SQLite\\ClassWarsMatchups.db";
        DBHandler.setSQLiteConnection(realDbLocation);
//        conn = DriverManager.getConnection(realDbLocation);
    }

    @Test
    public void testCorrectDatabaseSchema() throws SQLException {
        List<String> expectedTables = Arrays.asList(
                "Mercenary", "Objective", "GameMode", "Map", "Stage", "StageGameMode", "Matchup", "LegacyMatchup");
        String query = "SELECT name FROM sqlite_schema WHERE type = 'table' AND name NOT LIKE 'sqlite_%'";
        ResultSet getSchemaRS = conn.createStatement().executeQuery(query);
        List<String> foundTables = new ArrayList<>();
        while (getSchemaRS.next()) {
            String table = getSchemaRS.getString(1);
            Assert.assertTrue(expectedTables.contains(table));
            foundTables.add(table);
        }
        Assert.assertTrue(foundTables.containsAll(expectedTables));
    }

    @Test
    public void testAllMercenariesArePresentAndCorrect() throws SQLException {
        List<String> expectedMercenaries = Arrays.asList(
                "Scout", "Soldier", "Pyro", "Demoman", "Heavy", "Engineer", "Medic", "Sniper", "Spy");
        String query = "SELECT MercenaryName FROM Mercenary";
        ResultSet getMercenariesRS = conn.createStatement().executeQuery(query);
        List<String> foundMercenaries = new ArrayList<>();
        while (getMercenariesRS.next()) {
            String mercenary = getMercenariesRS.getString(1);
            Assert.assertTrue(expectedMercenaries.contains(mercenary));
            foundMercenaries.add(mercenary);
        }
        Assert.assertTrue(foundMercenaries.containsAll(expectedMercenaries));
    }

    @Test
    public void testIncrementOfWins() {

    }
}
