package Edstemus.GUI;

import Edstemus.Scroll.Scroll;
import Edstemus.database.ScrollData;
import Edstemus.Scroll.ScrollSearchOptions;
import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.database.UserData;
import Edstemus.view.DashboardScene;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdminInteraction {
    private static final ScrollData scrollData = new ScrollData();
    private static Tab ScrollTab = null;

    public static void addAdminTabs(){
        MainBoxMethods.clearMain();
        TabPane tabs = DashboardScene.Admintabs();

        ScrollTab = createScrollStatisticsTab();
        tabs.getTabs().add(ScrollTab);
        DashboardScene.MainBox.getChildren().add(tabs);
    }


    public static VBox getUsers(){

        HBox overall = new HBox();

        VBox left = new VBox();
        VBox right = new VBox();

        ScrollPane users = new ScrollPane();
        users.setFitToWidth(true);
        VBox container = new VBox(10);

        HBox adder = getadder();
        adder.prefWidthProperty().bind(right.widthProperty());

        UserData userdb = new UserData();
        ArrayList<String> dbusers = userdb.displayAllUsers();
        Collections.reverse(dbusers);

        HBox examiner = new HBox();

        adder.prefHeightProperty().bind(right.heightProperty().multiply(0.20));
        examiner.prefHeightProperty().bind(right.heightProperty().multiply(0.80));
        examiner.prefWidthProperty().bind(right.widthProperty());

        for (String dbuser : dbusers){
            container.getChildren().add(createUserBox(dbuser,examiner));
        }

        users.setContent(container);
        users.prefHeightProperty().bind(left.heightProperty());


        Label adduser = new Label("Add User");
        Label exuser = new Label("Examine User");
        Label dbuser = new Label("Database Users");

        right.getChildren().addAll(adduser,adder);
        right.getChildren().addAll(exuser,examiner);
        left.getChildren().addAll(dbuser,users);

        adduser.getStyleClass().add("subheader");
        exuser.getStyleClass().add("subheader");
        dbuser.getStyleClass().add("subheader");

        overall.getChildren().addAll(left,right);

        left.prefWidthProperty().bind(overall.widthProperty().multiply(0.5));
        right.prefWidthProperty().bind(overall.widthProperty().multiply(0.5));
        left.prefHeightProperty().bind(overall.heightProperty().multiply(.50));
        right.prefHeightProperty().bind(overall.heightProperty().multiply(.50));

        overall.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty());
        overall.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty().multiply(0.5));


        VBox bigger = new VBox();
        bigger.getChildren().add(overall);
        return bigger;
    }



    private static HBox createUserBox(String dbuser,HBox examiner){
        HBox temp = new HBox();
        UserData userdb = new UserData();

        if (userdb.getUser(dbuser).getType() != UserType.ADMIN){
            Button account = new Button(dbuser);
            account.prefWidthProperty().bind(temp.widthProperty().multiply(0.5));

            Button examine = new Button ("Examine");
            examine.setOnAction(event -> {
                examiner.getChildren().clear();
                getExaminer(examiner,userdb,dbuser);
            });
            examine.prefWidthProperty().bind(temp.widthProperty().multiply(0.25));

            Button delete = new Button("Delete");
            delete.setOnAction(event ->{
                boolean result = userdb.deleteUser(userdb.getUser(dbuser));
                if (result){
                    DashboardScene.refreshAdmintabs();
                    refreshStatistics();
                }
            });
            delete.prefWidthProperty().bind(temp.widthProperty().multiply(0.25));

            temp.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.5));
            temp.getChildren().addAll(account,examine,delete);
        }



        return temp;
    }



    private static HBox getadder(){
        HBox adder = new HBox();

        UserData userdb = new UserData();

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        usernameField.prefWidthProperty().bind(adder.widthProperty().multiply(0.25));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        passwordField.prefWidthProperty().bind(adder.widthProperty().multiply(0.25));

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.prefWidthProperty().bind(adder.widthProperty().multiply(0.25));

        Button adderButton = new Button("Add User");
        adderButton.prefWidthProperty().bind(adder.widthProperty().multiply(0.25));
        adderButton.setOnAction(event -> {

                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    String email = emailField.getText();

                    usernameField.setText("");
                    passwordField.setText("");
                    emailField.setText("");

                    User temp = new User(null, email, null,null,
                            username, password, UserType.NORMAL);

                    boolean result = userdb.insertUser(temp);
                    if (result) {
                        DashboardScene.refreshAdmintabs();
                        refreshStatistics();
                    }
                }
        );

        adder.getChildren().addAll(usernameField, passwordField,emailField,adderButton);

        return adder;
    }

    public static void getExaminer(HBox examiner,UserData userdb,String dbuser){

        User found_user = userdb.getUser(dbuser);
        if (found_user != null){
            TextArea details = new TextArea(found_user.getdetails(found_user));
            examiner.getChildren().addAll(details);
        }

    }

    private static Tab createScrollStatisticsTab(){
        Tab scrollStatsTab = new Tab("Scroll Statistics");
        scrollStatsTab.setClosable(false);
        ScrollPane scrollPane = getScrollStatisticsView();
        scrollStatsTab.setContent(scrollPane);
        return scrollStatsTab;
    }

    // Method to fetch and display statistics for all scrolls
    private static ScrollPane getScrollStatisticsView() {
        ScrollPane scrollPane = new ScrollPane();
        VBox container = new VBox(10); // Container to hold the scroll statistics

        Label ScrollStatsLab = new Label("Scroll Statistics (Sorted by Downloads)");
        ScrollStatsLab.getStyleClass().add("bigheader");
        // Header for scroll statistics
        container.getChildren().add(ScrollStatsLab);

        // Use an empty criteria object to avoid null pointer exception
        ScrollSearchOptions emptyCriteria = new ScrollSearchOptions();
        scrollData.loadScrollsFromDatabase();
        List<Scroll> allScrolls = scrollData.getScrollsByDownloadCount();

        // Display statistics for each scroll
        for (Scroll scroll : allScrolls) {
            HBox scrollBox = createScrollStatisticsBox(scroll);
            container.getChildren().add(scrollBox);
        }

        scrollPane.setContent(container);
        return scrollPane;
    }

    // Create a UI component to display statistics for each scroll
    private static HBox createScrollStatisticsBox(Scroll scroll) {
        HBox scrollBox = new HBox(20); // Box to display scroll data with spacing

        Label titleLabel = new Label("Title: " + scroll.getScrollName());
        titleLabel.getStyleClass().add("subheader");

        Label ownerLabel = new Label("OwnerID: " + scroll.getOwnerID());
        ownerLabel.getStyleClass().add("subheader");

        Label uploadDate = new Label("Upload Date: " + scroll.getUploadDate());
        uploadDate.getStyleClass().add("subheader");

        Label downloadsLabel = new Label("Downloads: " + scroll.getTotalDownloads());
        downloadsLabel.getStyleClass().add("subheader");

        // Increment download button
        Button incrementButton = new Button("Increment Download");
        incrementButton.setOnAction(event -> {
            scrollData.increaseDownload(scroll.getScrollID());  // Call increaseDownload method
            refreshStatistics();  // Refresh to show updated download count
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> {
            boolean result = scrollData.deleteScroll(scroll.getScrollID(), scroll.getOwnerID());
            if (result) {
                DashboardScene.refreshAdmintabs();  // Refresh admin tabs after deletion
                refreshStatistics();
            }
        });

        scrollBox.getChildren().addAll(titleLabel,ownerLabel,uploadDate,downloadsLabel, incrementButton, deleteButton);
        return scrollBox;
    }

    // Refresh the statistics view after an action (like increment or delete)
    private static void refreshStatistics() {
        //TabPane tabs = DashboardScene.Admintabs();
        //tabs.getTabs().remove(0);  // Remove the existing statistics tab
        if (ScrollTab != null){
            ScrollTab.setContent(getScrollStatisticsView()); // Add a fresh statistics tab
        }

    }

}
