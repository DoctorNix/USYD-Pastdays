package database_tests;

import Edstemus.Scroll.Scroll;
import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.database.DatabaseManager;
import Edstemus.database.ScrollData;
import Edstemus.database.UserData;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class InsertScrollScrollDataTest {
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

    @AfterAll
    public static void tearDown() {
        File dbFile = new File("src/test/test_resources/test_db/edstemus_test.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testInsertScroll(){
        // Add a user to satisfy the foreign key constraint for uploader_id
        String phoneNumber = "12345";
        String email = "example@test.com";
        String firstName = "John";
        String lastName = "Smith";
        String username = "john123";
        String password = "password";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);

        boolean success = userData.insertUser(user);
        assertTrue(success, "User should have been inserted successfully");


        int ownerID = 2;
        String scrollName = "Test_Scroll";
        byte[] scrollContent = new byte[]{1, 2, 3, 4};
        int totalDownloads = 0;
        LocalDate uploadDate = LocalDate.of(2024, 10, 11);

        Scroll scroll = new Scroll(ownerID, scrollName, scrollContent, totalDownloads, uploadDate);

        boolean result = scrollData.insertScroll(scroll);

        assertTrue(result, "Scroll should have been inserted");

        try(Connection conn = dbManager.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Scroll WHERE name = 'Test_Scroll';")){

            assertTrue(rs.next(), "Scroll should be in databse");

            assertEquals(ownerID, rs.getInt("uploader_id"));
            assertEquals(scrollName, rs.getString("name"));
            assertArrayEquals(scrollContent, rs.getBytes("content"));
            assertEquals(totalDownloads, rs.getInt("downloads"));
            assertEquals(java.sql.Date.valueOf(uploadDate), rs.getDate("upload_date"));
        } catch (SQLException e){
            e.printStackTrace();

        }

    }

}


