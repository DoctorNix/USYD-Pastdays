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

import static org.junit.jupiter.api.Assertions.*;

public class ScrollOfDayTests {
    private ScrollData scrollData;
    private UserData userData;
    private static DatabaseManager dbManager;

    @BeforeAll
    static void setUpOnce() {
        dbManager = DatabaseManager.getInstance();
        String test_url = "jdbc:sqlite:src/test/test_resources/test_db/edstemus_test.db";
        dbManager.setDatabasePath(test_url);
    }

    @BeforeEach
    void setUp() {
        scrollData = new ScrollData();
        userData = new UserData();
        dbManager.createUsersTable();
        dbManager.createScrollTable();
        dbManager.createScrollOfDayTable();
        scrollData.loadScrollsFromDatabase();
    }

    @AfterEach
    void cleanUpTables() {
        try (Connection conn = dbManager.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM Users");
            stmt.executeUpdate("DELETE FROM Scroll");
            stmt.executeUpdate("DELETE FROM ScrollOfTheDay");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void tearDown() {
        File dbFile = new File("src/test/test_resources/test_db/edstemus_test.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void testGetScrollOfTheDay() {
        Scroll scroll1 = new Scroll(1, "Scroll 1", new byte[0], 0, LocalDate.now());
        Scroll scroll2 = new Scroll(1, "Scroll 2", new byte[0], 0, LocalDate.now());
        scrollData.insertScroll(scroll1);
        scrollData.insertScroll(scroll2);

        Scroll scrollOfTheDay = scrollData.getScrollOfTheDay();
        assertNotNull(scrollOfTheDay, "Scroll of the Day should not be null");
        assertTrue(scrollOfTheDay.getScrollName().equals("Scroll 1") || scrollOfTheDay.getScrollName().equals("Scroll 2"),
                "Scroll of the Day should be one of the inserted scrolls");
    }

    @Test
    void testGetTodayScrollFromDatabase() {
        Scroll scroll = new Scroll(1, "Scroll 3", new byte[0], 0, LocalDate.now());
        boolean inserted = scrollData.insertScroll(scroll);
        assertTrue(inserted, "Scroll should be inserted successfully");

        Scroll insertedScroll = scrollData.getScroll(scroll.getScrollName());
        assertNotNull(insertedScroll, "Inserted scroll should be found");
        int scrollID = insertedScroll.getScrollID();
        System.out.println("Inserted Scroll ID: " + scrollID);

        LocalDate today = LocalDate.now();
        scrollData.saveTodayScrollToDatabase(today, scrollID);

        Scroll todayScroll = scrollData.getTodayScrollFromDatabase(today);
        assertNotNull(todayScroll, "Scroll should be found for today's date");
        System.out.println("Retrieved Today Scroll ID: " + todayScroll.getScrollID());
        assertEquals(scrollID, todayScroll.getScrollID(), "Scroll IDs should match");
    }

    @Test
    void testSaveTodayScrollToDatabase() {
        Scroll scroll = new Scroll(1, "Scroll 4", new byte[0], 0, LocalDate.now());
        boolean inserted = scrollData.insertScroll(scroll);
        assertTrue(inserted, "Scroll should be inserted successfully");

        Scroll insertedScroll = scrollData.getScroll(scroll.getScrollName());
        assertNotNull(insertedScroll, "Inserted scroll should be found");
        int scrollID = insertedScroll.getScrollID();
        System.out.println("Inserted Scroll ID: " + scrollID);

        LocalDate today = LocalDate.now();
        scrollData.saveTodayScrollToDatabase(today, scrollID);

        Scroll todayScroll = scrollData.getTodayScrollFromDatabase(today);
        assertNotNull(todayScroll, "Scroll should be saved for today's date");
        System.out.println("Retrieved Today Scroll ID: " + todayScroll.getScrollID());
        assertEquals(scrollID, todayScroll.getScrollID(), "Scroll IDs should match");
    }

    @Test
    void testResetScrollOfTheDayTable() {
        Scroll scroll = new Scroll(1, "Scroll 5", new byte[0], 0, LocalDate.now());
        scrollData.insertScroll(scroll);
        LocalDate today = LocalDate.now();
        scrollData.saveTodayScrollToDatabase(today, scroll.getScrollID());

        scrollData.resetScrollOfTheDayTable();

        Scroll todayScroll = scrollData.getTodayScrollFromDatabase(today);
        assertNull(todayScroll, "ScrollOfTheDay table should be reset, no scroll should be found");
    }

    @Test
    void testAllScrollsPicked() {
        Scroll scroll = new Scroll(1, "Scroll 6", new byte[0], 0, LocalDate.now());
        scrollData.insertScroll(scroll);

        LocalDate today = LocalDate.now();
        scrollData.saveTodayScrollToDatabase(today, scroll.getScrollID());

        Scroll newScrollOfTheDay = scrollData.getScrollOfTheDay();
        assertNotNull(newScrollOfTheDay, "After reset, there should still be a scroll of the day");
        assertEquals(scroll.getScrollID(), newScrollOfTheDay.getScrollID(), "Scroll ID should match since it's the only scroll");
    }
}
