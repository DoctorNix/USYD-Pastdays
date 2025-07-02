package Edstemus.view;

import Edstemus.User.User;

import Edstemus.User.UserLogin;
import Edstemus.User.UserType;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Edstemus.App;
import javafx.scene.control.Label;

public class LoginScene {
    private Scene scene;

    private TextField usernameField;
    private PasswordField passwordField;
    private UserLogin userLogin;

    public LoginScene(Scene old_scene) {
        userLogin = new UserLogin();

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        Label heading = new Label("Login");
        heading.getStyleClass().add("bigheader");

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(250);
        usernameField.setOnAction(event -> login());

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        passwordField.setOnAction(event -> login());

        HBox buttons = new HBox(10);
        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> login());
        loginButton.setMinWidth(80);
        loginButton.setMaxWidth(80);


        Button signupButton = new Button("Sign up");
        signupButton.setOnAction(event -> signup());
        signupButton.setMinWidth(80);
        signupButton.setMaxWidth(80);

        Button guestButton = new Button("Continue as Guest");
        guestButton.setOnAction(event -> continueAsGuest());
        guestButton.setMinWidth(170);
        guestButton.setMaxWidth(170);


        buttons.getChildren().addAll(loginButton,signupButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(heading,usernameField, passwordField, buttons,guestButton);
        if (old_scene != null) {
            scene = new Scene(layout, old_scene.getWidth(), old_scene.getHeight());
        }
        else{
            scene = new Scene(layout, 500, 500);
        }

        scene.getStylesheets().add(getClass().getResource("/styles/Style.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }


    private void login(){

        String username = usernameField.getText();
        String password = passwordField.getText();

        usernameField.setText("");
        passwordField.setText("");

        if (userLogin.validateLogin(username,password)) {
            System.out.println("Login Successful");

            User current_user = userLogin.getUser(username);

            if (current_user != null) {
                App.showDashboardScene(current_user);
            } else {
                System.out.println("Error: User not found after login.");
            }

        } else {
            System.out.println("Invalid login: Please check your username and password.");
        }
    }

    private void continueAsGuest() {
        User guestUser = new User("", "", "", "", "GUEST USER", "", UserType.GUEST);
        App.showDashboardScene(guestUser);
    }

    private void signup(){
        App.showSignupScene();
        //TO DO
    }

}
