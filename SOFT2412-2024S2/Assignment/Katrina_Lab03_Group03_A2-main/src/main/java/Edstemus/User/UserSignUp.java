package Edstemus.User;

import Edstemus.database.UserData;

public class UserSignUp {


    public boolean signup(User user) {
        if(!validateInput(user)) {
            System.out.println("Inputs are invalid");
            return false;
        }

        UserData userData = new UserData();
        boolean result = userData.insertUser(user);

        if(result) {
            System.out.println("User has been successfully signed up");
            return true;
        } else {
            System.out.println("User signed up failed");
            return false;
        }
    }

    private boolean validateInput(User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            System.out.println("Email is required.");
            return false;
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            System.out.println("Username is required.");
            return false;
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            System.out.println("Password is required.");
            return false;
        }

        return true;
    }
}
