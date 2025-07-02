import soft2412.Users.User;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import soft2412.Users.UserRole;

class UserTest {

    // Test for creating a normal user.
    @Test
    void testNormalUserCreation() {
        String expectedUsername = "testUser1";
        UserRole expectedRole = UserRole.NORMAL;
        
        // Create a Normal user.
        User testUser = User.createNormal(expectedUsername);
        
        // Verify that the username and role are set correctly as normal user.
        assertEquals(expectedUsername, testUser.getUsername(), "Username not set correctly");
        assertEquals(expectedRole, testUser.getRole(), "Role not set correctly");
        assertFalse(testUser.isAdmin(), "Admin not set correctly");
    }
    
    // Test for creating an admin user.
    @Test
    void testAdminUserCreation() {

        String expectedUsername = "adminUser1";
        UserRole expectedRole = UserRole.ADMIN;
        
        // Create an Admin user.
        User adminUser = User.createAdmin(expectedUsername);
        
        // Verify that the username and role are set correctly as admin user.
        assertEquals(expectedUsername, adminUser.getUsername(), "Username not set correctly");
        assertEquals(expectedRole, adminUser.getRole(), "Role not set correctly");
        assertTrue(adminUser.isAdmin(), "Admin user not set correctly");
    }

}

