package database_tests;

import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.database.DatabaseManager;
import Edstemus.database.UserData;
import Edstemus.database.security.PasswordHasher;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataTest {
    private UserData userData;
    private static DatabaseManager dbManager;

    @BeforeAll
    public static void setUpOnce() {
        dbManager = DatabaseManager.getInstance();
        String test_url = ("jdbc:sqlite:src/test/test_resources/test_db/edstemus_test.db");
        dbManager.setDatabasePath(test_url);
    }

    @BeforeEach
    public void setUp(){
        userData = new UserData();
        dbManager.createUsersTable();
        userData.loadUsersFromDatabase();
    }

    @AfterEach
    public void cleanUpTables() {
        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Users");
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
    public void testInsertUser(){
        String phoneNumber = "12345";
        String email = "example@test.com";
        String firstName = "John";
        String lastName = "Smith";
        String username = "john123";
        String password = "password";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);

        boolean result = userData.insertUser(user);
        assertTrue(result, "User should have been inserted successfully");

        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM Users WHERE username='john123';")) {

            assertTrue(rs.next(), "User should have been inserted");

            assertEquals(phoneNumber, rs.getString("phoneNumber"));
            assertEquals(email, rs.getString("email"));
            assertEquals(firstName, rs.getString("firstName"));
            assertEquals(lastName, rs.getString("lastName"));
            assertEquals(username, rs.getString("username"));

            assertEquals(type, UserType.valueOf(rs.getString("userType")));

            String storedPasswordHash = rs.getString("password");
            assertTrue(PasswordHasher.verifyPassword(password, storedPasswordHash), "Password hash should match the original password");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAddingAndDeleteUser(){
        String phoneNumber = "17812";
        String email = "titans@gundam.com";
        String firstName = "Amuro";
        String lastName = "Ray";
        String username = "RX178-2";
        String password = "password123";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);
        boolean InsertResult = userData.insertUser(user);

        assertTrue(InsertResult, "User should have been inserted successfully");

        // for double check
        String InsertedPassword = userData.getPasswordForUser(user.getUsername());

        assertNotNull(InsertedPassword, "User should exist in the database after insert");
        System.out.println("Inserted Password Hash: " + InsertedPassword);  // Debugging line

        // Check if the password hash matches
        try {
            assertTrue(PasswordHasher.verifyPassword(password, InsertedPassword), "Password hash should match the original password");
        } catch (IllegalArgumentException e) {
            System.out.println("Exception in Password Verification: " + e.getMessage());
            fail("Password verification failed due to an unexpected format or null value.");
        }

        boolean deleteResult = userData.deleteUser(user);
        assertTrue(deleteResult, "User should have been deleted");

        InsertedPassword = userData.getPasswordForUser(username);
        assertNull(InsertedPassword, "User should not exist in the database after deletion");
    }

    @Test
    public void testDeleteNonExistingUser(){
        String phoneNumber = "123";
        String email = "v@wb.com";
        String firstName = "A";
        String lastName = "R";
        String username = "RX78-2";
        String password = "whitebase";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);

        boolean DeleteResult = userData.deleteUser(user);
        assertFalse(DeleteResult, "User should not have been deleted since it not exists");
    }

    @Test
    public void tetsUpdateUserField(){
        String phoneNumber = "111";
        String email = "test@test.com";
        String firstName = "John";
        String lastName = "Smith";
        String username = "john123";
        String password = "password123";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);
        boolean userInsert = userData.insertUser(user);
        assertTrue(userInsert, "User should have been inserted successfully");

        String newEmail = "new@test.com";
        userData.updateUserField(username, "email", newEmail);
        assertEquals(newEmail, userData.getUser(username).getEmail(), "email not updated");

        String newFirstName = "Bob";
        userData.updateUserField(username, "firstName", newFirstName);
        assertEquals(newFirstName, userData.getUser(username).getFirstName(), "firstName not updated");

        String newLastName = "Gorb";
        userData.updateUserField(username, "lastName", newLastName);
        assertEquals(newLastName, userData.getUser(username).getLastName(), "lastName not updated");

        String newPhoneNumber  = "222";
        userData.updateUserField(username, "phoneNumber", newPhoneNumber);
        assertEquals(newPhoneNumber, userData.getUser(username).getPhoneNumber(), "phoneNumber not updated");

        String newUsername = "bob123";
        userData.updateUserField(username, "username", newUsername);
        assertEquals(newUsername, userData.getUser(newUsername).getUsername(), "username not updated");

        String newPassword = "newpassword";
        userData.updateUserField(newUsername, "password", newPassword);
        String updatedPassword = userData.getPasswordForUser(newUsername);
        assertTrue(PasswordHasher.verifyPassword(newPassword, updatedPassword), "password not updated");
    }

    @Test
    public void testGetIdFromUser(){
        String phoneNumber = "111";
        String email = "test@test.com";
        String firstName = "John";
        String lastName = "Smith";
        String username = "john123";
        String password = "password123";
        UserType type = UserType.NORMAL;

        User user = new User(phoneNumber, email, firstName, lastName, username, password, type);
        boolean userInsert = userData.insertUser(user);
        assertTrue(userInsert, "User should have been inserted successfully");

        int id = userData.getIdFromUser(username);
        assertTrue(id != 0, "User should have been inserted successfully");
    }
}
