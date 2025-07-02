package Edstemus.GUI;

import Edstemus.App;
import Edstemus.User.User;
import Edstemus.database.UserData;
import Edstemus.view.DashboardScene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class ProfileInteraction {

    private static TextField usernameField;
    private static PasswordField passwordField;
    private static TextField phoneField;
    private static TextField emailField;
    private static TextField fnameField;
    private static TextField lnameField;

    public static void addUserOptions(User current_user){
        MainBoxMethods.clearMain();

        VBox profile = getProfile(current_user);
        profile.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty());

        DashboardScene.MainBox.getChildren().add(profile);
    }


    public static VBox getProfile(User current_user){

        VBox profile = new VBox(10);

        Label Heading = new Label("Your Profile");
        Heading.getStyleClass().add("bigheader");

        HBox user = new HBox(10);
        Label userheading = new Label("Username: ");
        usernameField = new TextField();
        usernameField.setText(current_user.getUsername());
        usernameField.setMaxWidth(250);
        user.getChildren().addAll(userheading,usernameField);

        HBox pass = new HBox(10);
        Label passheading = new Label("Password: ");
        passwordField = new PasswordField();
        passwordField.setText(current_user.getPassword());
        passwordField.setMaxWidth(250);
        pass.getChildren().addAll(passheading,passwordField);

        HBox email = new HBox(10);
        Label emailheading = new Label("Email: ");
        emailField = new TextField();
        emailField.setText(current_user.getEmail());
        emailField.setMaxWidth(250);
        email.getChildren().addAll(emailheading,emailField);

        HBox phone = new HBox(10);
        Label phoneheading = new Label("Phone: ");
        phoneField = new TextField();
        phoneField.setText(current_user.getPhoneNumber());
        phoneField.setMaxWidth(250);
        phone.getChildren().addAll(phoneheading,phoneField);

        HBox fname = new HBox(10);
        Label fnameheading = new Label("First Name: ");
        fnameField = new TextField();
        fnameField.setText(current_user.getFirstName());
        fnameField.setMaxWidth(250);
        fname.getChildren().addAll(fnameheading,fnameField);

        HBox lname = new HBox(10);
        Label lnameheading = new Label("Last Name: ");
        lnameField = new TextField();
        lnameField.setText(current_user.getLastName());
        lnameField.setMaxWidth(250);
        lname.getChildren().addAll(lnameheading,lnameField);

        profile.setAlignment(Pos.CENTER);

        double LabelWidth = 50;
        fnameheading.setMinWidth(LabelWidth);
        fname.setAlignment(Pos.CENTER);
        lnameheading.setMinWidth(LabelWidth);
        lname.setAlignment(Pos.CENTER);
        userheading.setMinWidth(LabelWidth);
        user.setAlignment(Pos.CENTER);
        passheading.setMinWidth(LabelWidth);
        pass.setAlignment(Pos.CENTER);
        emailheading.setMinWidth(LabelWidth);
        email.setAlignment(Pos.CENTER);
        phoneheading.setMinWidth(LabelWidth);
        phone.setAlignment(Pos.CENTER);

        HBox optionbuttons = new HBox(10);
        Button reset = new Button("Reset");
        reset.setPrefWidth(120);
        reset.setOnAction(event -> resetProfile(current_user));

        Button update = new Button("Update");
        update.setOnAction(event -> updateProfile(current_user));
        update.setPrefWidth(120);
        optionbuttons.getChildren().addAll(reset,update);
        optionbuttons.setAlignment(Pos.CENTER);


        profile.getChildren().addAll(Heading,fname,lname,user,pass,email,phone,optionbuttons);
        return profile;
    }

    public static void resetProfile(User current_user){
        usernameField.setText(current_user.getUsername());
        passwordField.setText(current_user.getPassword());
        phoneField.setText(current_user.getPhoneNumber());
        emailField.setText(current_user.getEmail());
        fnameField.setText(current_user.getFirstName());
        lnameField.setText(current_user.getLastName());
    }

    private static void updateProfile(User current_user){

        UserData userdb = new UserData();

        String username = usernameField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String fname = fnameField.getText();
        String lname = lnameField.getText();

        if (!Objects.equals(username, current_user.getUsername())){
            userdb.updateUserField(current_user.getUsername(), "username", username);
            App.showLoginScene();
        }

        if (!Objects.equals(password, current_user.getPassword())){
            userdb.updateUserField(current_user.getUsername(), "password", password);
        }

        if (!Objects.equals(phone, current_user.getPhoneNumber())){
            userdb.updateUserField(current_user.getUsername(), "phoneNumber", phone);
        }

        if (!Objects.equals(email, current_user.getEmail())){
            userdb.updateUserField(current_user.getUsername(), "email", email);
        }

        if (!Objects.equals(fname, current_user.getFirstName())){
            userdb.updateUserField(current_user.getUsername(), "firstName", fname);
        }

        if (!Objects.equals(lname, current_user.getLastName())){
            userdb.updateUserField(current_user.getUsername(), "lastName", lname);
        }
    }

}
