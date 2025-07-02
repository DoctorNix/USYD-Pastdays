package database_tests;

import Edstemus.Scroll.Scroll;
import Edstemus.database.DatabaseManager;
import Edstemus.database.ScrollData;
import Edstemus.database.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class DeleteScrollTest {

    private static DatabaseManager dbManager;
    private ScrollData scrollData;
    private UserData userData;

    @BeforeAll
    public static void setUpOnce() {
        dbManager = DatabaseManager.getInstance();
        String testUrl = "jdbc:sqlite:src/test/test_resources/test_db/edstemus_test.db";
        dbManager.setDatabasePath(testUrl);
    }

    @BeforeEach
    public void setUp() {
        scrollData = new ScrollData();
        userData = new UserData();
        dbManager.createUsersTable();
        dbManager.createScrollTable();
        scrollData.loadScrollsFromDatabase();
    }

    @AfterEach
    public void cleanUpTables() {
        try (Connection conn = dbManager.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Users");
            stmt.executeUpdate("DELETE FROM Scroll");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown() {
        File dbFile = new File("src/test/test_resources/test_db/edstemus_test.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testDeleteScrollPass() {
        Scroll scroll = new Scroll(1, "Test Scroll", "Sample content".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(scroll);

        int scrollID = scroll.getScrollID();
        int ownerID = scroll.getOwnerID();

        boolean isDeleted = scrollData.deleteScroll(scrollID, ownerID);

        assertTrue(isDeleted, "Scroll should be deleted");

        assertNull(scrollData.getScroll(scroll.getScrollName()), "Scroll should not exist");
    }

    @Test
    public void testDeleteScrollNull() {
        boolean isDeleted = scrollData.deleteScroll(999, 1);

        assertFalse(isDeleted, "Deletion should fail");
    }

    @Test
    public void testDeleteScrollUnauthorised() {
        Scroll scroll = new Scroll(1, "Test Scroll", "Sample content".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(scroll);

        int scrollID = scroll.getScrollID();

        boolean isDeleted = scrollData.deleteScroll(scrollID, 2);

        assertFalse(isDeleted, "Deletion should fail for an unauthorised user");
    }
}
