package database_tests;

import Edstemus.Scroll.Scroll;
import Edstemus.database.DatabaseManager;
import Edstemus.database.ScrollData;
import Edstemus.database.UserData;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DisplayAllScrollsTest {
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
    public void testDisplayAllScrolls() {
        Scroll scroll1 = new Scroll(1, "Test Scroll 1", "Sample content 1".getBytes(), 0, LocalDate.now());
        Scroll scroll2 = new Scroll(2, "Test Scroll 2", "Sample content 2".getBytes(), 0, LocalDate.now());
        Scroll scroll3 = new Scroll(3, "Test Scroll 3", "Sample content 3".getBytes(), 0, LocalDate.now());

        scrollData.insertScroll(scroll1);
        scrollData.insertScroll(scroll2);
        scrollData.insertScroll(scroll3);

        ArrayList<String> scrollNames = scrollData.displayAllScrolls();

        assertNotNull(scrollNames, "Scroll names list should not be null");
        assertEquals(3, scrollNames.size(), "There should be 3 scroll names in the list");
        assertTrue(scrollNames.contains("Test Scroll 1"), "Scroll names should contain 'Test Scroll 1'");
        assertTrue(scrollNames.contains("Test Scroll 2"), "Scroll names should contain 'Test Scroll 2'");
        assertTrue(scrollNames.contains("Test Scroll 3"), "Scroll names should contain 'Test Scroll 3'");
    }
}
