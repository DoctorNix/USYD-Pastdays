import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import soft2412.SignUpManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SignupManagerTest {
    SignUpManager signUpManager;
    String username = "testUser";
    String password = "testPassword";
    File userFile;
    
    // Initializes the SignUpManager and prepares the user file path before each test.
    @BeforeEach
    void setUp() {
        signUpManager = new SignUpManager();
        userFile = new File("src/TestDatabase/TestNormalUsers/" + username + ".txt");

    }
    
    // Tests for creating a normal user and verifying the user file and password are stored correctly.
    @Test
    void testCreateNormalUser() {
        signUpManager.setDirectoryPath("src/TestDatabase/TestNormalUsers");
        signUpManager.createUser(username, password);
        assertTrue(userFile.exists(), "User does not exist");

        try{
            BufferedReader br = new BufferedReader(new FileReader(userFile));
            String storedPassword = br.readLine();
            assertEquals(password, storedPassword, "Wrong password");
            br.close();
        } catch (IOException e) {
            fail("Error reading user file");
        }
    }

    // Test for creating a new directory and ensuring the user file is created in that directory.
    @Test
    void testDirectoryCreation(){
        String directoryPath = "src/TestDatabase/TestNewUsers";
        signUpManager.setDirectoryPath(directoryPath);
        File directory = new File(directoryPath);

        signUpManager.createUser(username, password);

        assertTrue(directory.exists(), "Directory does not exist");

        File createdUserFile = new File(directory, username + ".txt");
        if (createdUserFile.exists()){
            // Clean up: delete the created user after the test.
            assertTrue(createdUserFile.delete());
        }
        // Clean up: delete the directory.
        directory.delete();
    }

    // Using tearDown method after each test to clean up the user file created during the test.
    @AfterEach
    void tearDown(){
        if (userFile.exists()){
            assertTrue(userFile.delete());
        }
    }

}
