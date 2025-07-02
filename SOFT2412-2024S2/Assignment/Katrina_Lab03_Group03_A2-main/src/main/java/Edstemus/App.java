package Edstemus;

import Edstemus.User.User;
import Edstemus.database.DatabaseManager;
import Edstemus.view.DashboardScene;
import Edstemus.view.LoginScene;
import Edstemus.view.SignupScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {
    private static Stage primaryStage;

    public static void main(String[] args) {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        dbManager.createUsersTable();

        dbManager.createScrollTable();

        dbManager.createScrollOfDayTable();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage){
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Edstemus");

        showLoginScene();
    }

    public static void showLoginScene(){
        Scene old_scene = primaryStage.getScene();
        LoginScene loginScene = new LoginScene(old_scene);

        primaryStage.setScene(loginScene.getScene());
        //setstagedim();
        primaryStage.show();
    }

    public static void showDashboardScene(User current_user) {
        Scene old_scene = primaryStage.getScene();
        DashboardScene dashboardScene = new DashboardScene(old_scene,primaryStage,current_user);

        primaryStage.setScene(dashboardScene.getScene());

        //setstagedim();
        primaryStage.show();

    }

    public static void showSignupScene(){
        Scene old_scene = primaryStage.getScene();
        SignupScene signupScene = new SignupScene(old_scene);

        primaryStage.setScene(signupScene.getScene());

        //setstagedim();
        primaryStage.show();

    }
}