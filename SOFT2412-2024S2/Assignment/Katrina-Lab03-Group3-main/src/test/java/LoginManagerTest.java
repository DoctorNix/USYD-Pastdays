import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soft2412.LoginManager;
import soft2412.Users.User;
import soft2412.Users.UserRole;

class LoginManagerTest {

    LoginManager testloginmanager;

    // Initializes the LoginManager and sets the paths to the user directories for admin and normal users before each test.
    @BeforeEach
    void setUp() throws Exception {
        testloginmanager = new LoginManager();
        testloginmanager.setDirectoryPathAdminUsers("src/TestDatabase/TestAdminUsers");
        testloginmanager.setDirectoryPathNormalUsers("src/TestDatabase/TestNormalUsers");
    }

    // Test for a successful login of a normal user.
    @Test
    void testNormalLogin() {
        // Check if the normal user credentials are valid and the login is successful
        User result = testloginmanager.checkUser("realuser", "rightPassword");
        assertNotNull(result, "login successful");
        assertEquals("realuser", result.getUsername(), "username doesnt match");
        assertEquals(UserRole.NORMAL, result.getRole(), "role doesnt match");
        // Double check the user role is not Admin.
        assertFalse(result.isAdmin(), "user is not admin");
    }

    // Test for a successful login of an admin user.
    @Test
    void testAdminLogin() {
        // Check if the admin user credentials are valid and the login is successful
        User result = testloginmanager.checkUser("testAdmin", "strongrightPassword");
        assertNotNull(result, "login successful");
        assertEquals("testAdmin", result.getUsername(), "username doesnt match");
        assertEquals(UserRole.ADMIN, result.getRole(), "role doesnt match");
        // Double check the user role is Admin indeed.
        assertTrue(result.isAdmin(), "user is admin");

    }

    // Test for logging in with a non-existent user.
    @Test
    void testLoginNonExistentUser() {
        assertNull(testloginmanager.checkUser("fakeuser", "fakepassword"), "User not found");
    }

    // Test for a failed login attempt with an incorrect password for a normal user.
    @Test
    void testIncorrectNormalPasswordLogin() {
        assertNull(testloginmanager.checkUser("realuser", "wrongpassword"), "Incorrect password");
    }

    // Test for a failed login attempt with an incorrect password for an admin user.
    @Test
    void testIncorrectAdminPasswordLogin() {
        assertNull(testloginmanager.checkUser("testAdmin", "strongwrongPassword"), "Incorrect password");
    }

}

