package soft2412;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SignUpManager {
    private String DIRECTORY_PATH = "src/Database/NormalUsers";

    public void setDirectoryPath(String directoryPath) {
        this.DIRECTORY_PATH = directoryPath;
    }

    public void createUser(String username, String password) {
        File directory = new File(DIRECTORY_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File userFile = new File(directory, username + ".txt");
        try (FileWriter writer = new FileWriter(userFile)) {
            writer.write(password);
            System.out.println("User " + username + " registered successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while registering the user.");
            e.printStackTrace();
        }
    }
}
