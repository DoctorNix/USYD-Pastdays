package database_tests;
import Edstemus.database.DatabaseManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {
    private DatabaseManager dbManager;


    @BeforeAll
    public static void setUpOnce() {
        DatabaseManager.getInstance().setDatabasePath("jdbc:sqlite:src/test/test_resources/test_db/edstemus_test.db");
    }

    @BeforeEach
    public void setUp(){
        dbManager = DatabaseManager.getInstance();
        dbManager.createUsersTable();
        dbManager.createScrollTable();
    }

    @AfterEach
    public void cleanUpTables() {
        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Users");
            st.executeUpdate("DELETE FROM Scroll");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown(){
        File dbFile = new File("src/test/test_resources/test_db/edstemus_test.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }


    @Test
    public void testCreateUsersTable(){
        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Users';")) {

            assertTrue(rs.next(), "User table created");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertAdminUser() {
        dbManager.insertAdminUser();

        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Users WHERE username = 'admin123';")) {

            assertTrue(rs.next(), "Admin user should be inserted into Users table");
            assertEquals("admin123", rs.getString("username"), "Admin username should match");

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException occurred while verifying admin user insertion");
        }
    }

    @Test
    public void testCreateScrollTable() {
        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Scroll';")) {

            assertTrue(rs.next(), "Scroll table should be created");

        } catch (SQLException e) {
            e.printStackTrace();
            fail("SQLException occurred while verifying Scroll table creation");
        }
    }


}