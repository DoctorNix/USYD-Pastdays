package Edstemus.User;

import Edstemus.database.UserData;
import Edstemus.database.security.PasswordHasher;

public class UserLogin {
    private UserData userData;

    public UserLogin() {
        userData = new UserData();
    }

    public boolean validateLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please enter the username and password.");
            return false;
        }

        String storedPasswordHash = userData.getPasswordForUser(username);

        if (storedPasswordHash == null) {
            System.out.println("Username not found");
            return false;
        } else if (!PasswordHasher.verifyPassword(password, storedPasswordHash)) {
            System.out.println("Password does not match");
            return false;
        }
        return true;
    }

    public User getUser(String username) {
        return userData.getUser(username);
    }
}