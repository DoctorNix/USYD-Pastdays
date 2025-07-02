package Edstemus.view;

import Edstemus.App;
import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.User.UserSignUp;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;


public class SignupScene {

    private Scene scene;

    private TextField usernameField;
    private PasswordField passwordField;
    private TextField phoneField;
    private TextField emailField;
    private TextField fnameField;
    private TextField lnameField;

    public SignupScene(Scene old_scene) {

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        Label heading = new Label("Signup");
        heading.getStyleClass().add("bigheader");

        usernameField = new TextField();
        usernameField.setPromptText("Username*");
        usernameField.setMaxWidth(250);
        //usernameField.setAlignment(Pos.CENTER);

        passwordField = new PasswordField();
        passwordField.setPromptText("Password*");
        passwordField.setMaxWidth(250);
        //passwordField.setAlignment(Pos.CENTER);

        emailField = new TextField();
        emailField.setPromptText("Email*");
        emailField.setMaxWidth(250);

        phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.setMaxWidth(250);

        fnameField = new TextField();
        fnameField.setPromptText("First Name");
        fnameField.setMaxWidth(250);

        lnameField = new TextField();
        lnameField.setPromptText("Last Name");
        lnameField.setMaxWidth(250);



        HBox buttons = new HBox(10);
        Button signupButton = new Button("Signup");
        signupButton.setOnAction(event -> sign());
        signupButton.setMinWidth(80);
        signupButton.setMaxWidth(80);

        Button menuButton = new Button("Menu");
        menuButton.setOnAction(event -> Menu());
        menuButton.setMinWidth(80);
        menuButton.setMaxWidth(80);

        buttons.getChildren().addAll(signupButton,menuButton);
        buttons.setAlignment(Pos.CENTER);

        layout.getChildren().addAll(heading,usernameField, passwordField,emailField,phoneField,
                lnameField,fnameField,buttons);

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

    public void sign(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String fname = fnameField.getText();
        String lname = lnameField.getText();

        usernameField.setText("");
        passwordField.setText("");
        emailField.setText("");
        phoneField.setText("");
        fnameField.setText("");
        lnameField.setText("");

        User newuser = new User(phone, email, fname, lname, username, password, UserType.NORMAL);

        UserSignUp signer = new UserSignUp();

        System.out.println(newuser.getType().name());

        boolean result = signer.signup(newuser);

        if (result){
            App.showDashboardScene(newuser);
        } else{
            Alert a =  new Alert(AlertType.ERROR);
            a.setContentText("Error: One or more of the required fields is missing");
            a.show();
            System.out.println("sign up failed");
        }
    }


    public void Menu(){
        App.showLoginScene();
    }
}
