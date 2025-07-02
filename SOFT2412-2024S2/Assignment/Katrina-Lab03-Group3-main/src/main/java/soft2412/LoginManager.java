package soft2412;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import soft2412.Users.User;

public class LoginManager {
    private String DIRECTORY_PATH_NORMAL_USERS = "src/Database/NormalUsers";
    private String DIRECTORY_PATH_ADMIN_USERS = "src/Database/AdminUsers";

    public void setDirectoryPathNormalUsers(String path) {
        this.DIRECTORY_PATH_NORMAL_USERS = path;
    }

    public void setDirectoryPathAdminUsers(String path) {
        this.DIRECTORY_PATH_ADMIN_USERS = path;
    }

    public User checkUser(String username, String password) {
        File adminUserFile = new File(DIRECTORY_PATH_ADMIN_USERS, username + ".txt");
        if (adminUserFile.exists()) {
            if (check(adminUserFile, password)) {
                System.out.println("Admin login successful!");
                return User.createAdmin(username);
            } else {
                System.out.println("Incorrect password.");
                return null;
            }
        }

        File normalUserFile = new File(DIRECTORY_PATH_NORMAL_USERS, username + ".txt");
        if (normalUserFile.exists()) {
            if (check(normalUserFile, password)) {
                return User.createNormal(username);
            } else {
                System.out.println("Incorrect password.");
                return null;
            }
        }

        System.out.println("User not found.");
        return null;
    }

    private boolean check(File userFile, String password) {
        try {
            Scanner scanner = new Scanner(userFile);
            String storedPassword = scanner.nextLine();
            return storedPassword.equals(password);
        } catch (FileNotFoundException e) {
            return false;
        }
    }
}


