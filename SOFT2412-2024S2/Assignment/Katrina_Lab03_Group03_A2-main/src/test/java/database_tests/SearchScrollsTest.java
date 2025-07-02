package database_tests;

import Edstemus.GUI.SearchField;
import Edstemus.Scroll.Scroll;
import Edstemus.Scroll.ScrollSearchOptions;
import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.database.DatabaseManager;
import Edstemus.database.ScrollData;
import Edstemus.database.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchScrollsTest {
    private ScrollData scrollData;
    private UserData userData;
    private static DatabaseManager dbManager;

    @BeforeAll
    public static void setUpOnce() {
        dbManager = DatabaseManager.getInstance();
        String test_url = "jdbc:sqlite:src/test/test_resources/test_db/edstemus_test.db";
        dbManager.setDatabasePath(test_url);
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

    private void insertTestUser() {
        String phoneNumber = "12345";
        String email = "example@test.com";
        String firstName = "John";
        String lastName = "Smith";
        String username = "john123";
        String password = "password";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);
        userData.insertUser(user);
    }

    private Scroll insertTestScroll() {
        int ownerID = 2;
        String scrollName = "Test_Scroll";
        byte[] scrollContent = new byte[]{1, 2, 3, 4};
        int totalDownloads = 0;
        LocalDate uploadDate = LocalDate.of(2024, 10, 11);

        Scroll scroll = new Scroll(ownerID, scrollName, scrollContent, totalDownloads, uploadDate);
        scrollData.insertScroll(scroll);
        return scroll;
    }

    @Test
    public void testSearchByScrollName() {
        insertTestUser();
        Scroll scroll = insertTestScroll();

        ScrollSearchOptions options = new ScrollSearchOptions();
        options.addCriteria(SearchField.SCROLL_NAME, scroll.getScrollName());

        List<Scroll> result = scrollData.searchScrolls(options);

        assertEquals(1, result.size(), "There should be a scroll found");
        assertEquals("Test_Scroll", result.get(0).getScrollName(), "Scroll name should match");
    }

    @Test
    public void testSearchByScrollID() {
        insertTestUser();
        Scroll scroll = insertTestScroll();
        scroll.setScrollID(1);

        ScrollSearchOptions options = new ScrollSearchOptions();
        options.addCriteria(SearchField.SCROLL_ID, scroll.getScrollID());

        List<Scroll> result = scrollData.searchScrolls(options);

        assertEquals(1, result.size(), "There should be a scroll found");
        assertEquals(scroll.getScrollID(), result.get(0).getScrollID(), "Scroll ID should match");
    }

    @Test
    public void testSearchByUploaderID() {
        insertTestUser();
        Scroll scroll = insertTestScroll();

        ScrollSearchOptions options = new ScrollSearchOptions();
        options.addCriteria(SearchField.OWNER_ID, scroll.getOwnerID());

        List<Scroll> result = scrollData.searchScrolls(options);

        assertEquals(1, result.size(), "There should be a scroll found");
        assertEquals(scroll.getOwnerID(), result.get(0).getOwnerID(), "Uploader ID should match");
    }

    @Test
    public void testSearchByUploadDateRange() {
        insertTestUser();
        Scroll scroll = insertTestScroll();

        ScrollSearchOptions options = new ScrollSearchOptions();
        options.addCriteria(SearchField.UPLOAD_DATE_FROM, LocalDate.of(2024, 10, 1));
        options.addCriteria(SearchField.UPLOAD_DATE_TO, LocalDate.of(2024, 10, 15));

        List<Scroll> result = scrollData.searchScrolls(options);

        assertEquals(1, result.size(), "There should be 1 scroll found within the date range");
        assertEquals(scroll.getUploadDate(), result.get(0).getUploadDate(), "Upload date should match");
    }

    @Test
    public void testSearchByDownloadsRange() {
        insertTestUser();
        Scroll scroll = insertTestScroll();

        ScrollSearchOptions options = new ScrollSearchOptions();
        options.addCriteria(SearchField.MIN_DOWNLOADS, 0);
        options.addCriteria(SearchField.MAX_DOWNLOADS, 5);

        List<Scroll> result = scrollData.searchScrolls(options);

        assertEquals(1, result.size(), "There should be a scroll found with downloads in the range");
        assertEquals(scroll.getTotalDownloads(), result.get(0).getTotalDownloads(), "Downloads should match");
    }

}
