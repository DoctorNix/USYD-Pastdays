package Edstemus.view;

import Edstemus.App;
import Edstemus.GUI.*;
import Edstemus.Scroll.FileConvertor;
import Edstemus.Scroll.Scroll;
import Edstemus.User.User;

import Edstemus.User.UserType;
import Edstemus.database.ScrollData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DashboardScene {
    private Scene scene;

    private Stage primaryStage;
    private User current_user;
    private BorderPane pane;

    public static VBox MainBox;
    private static Tab users;

    public DashboardScene(Scene old_scene, Stage PrimaryStage, User current_user) {
        this.primaryStage = PrimaryStage;
        this.current_user = current_user;

        pane = new BorderPane();
        //HBox TopBar = getTopBar();

        VBox SideBar = getSidebar();
        VBox MainBox = getMainbox();

        //pane.setTop(TopBar);
        pane.setLeft(SideBar);
        pane.setCenter(MainBox);

        if (old_scene != null) {
            scene = new Scene(pane, old_scene.getWidth(), old_scene.getHeight());
        }
        else{

            scene = new Scene(pane, 500, 500);
        }

        scene.getStylesheets().add(getClass().getResource("/styles/Style.css").toExternalForm());
    }

    public Scene getScene() {
        return scene;
    }

    private void logout(){
        App.showLoginScene();
    }


    private VBox getSidebar(){


        double buttonwidth = 0.18;
        double buttonheight = 0.05;

        VBox SideBar = new VBox(10);
        SideBar.setStyle("-fx-background-color: #A9A9A9;");
        SideBar.setPadding(new Insets(10, 10, 10, 10));


        VBox Mast = new VBox(10);
        Mast.setAlignment(Pos.CENTER);

        try {
            FileInputStream inputstream = new FileInputStream("src/main/resources/images/Wizard's_Scroll.png");
            Image image = new Image(inputstream);
            ImageView scrollView = new ImageView(image);
            scrollView.setFitHeight(100);
            scrollView.setFitWidth(100);
            Mast.getChildren().add(scrollView);
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
            Label Company = new Label("");
            Mast.getChildren().add(Company);
        }

        Mast.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.20));
        Mast.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.20));

        VBox TextHeadings = new VBox(10);

        Separator sep = new Separator();

        Label Edstemus = new Label("Edstemus");
        Edstemus.getStyleClass().add("bigheader");
        Separator sep2 = new Separator();
        Label UserLabel = new Label(String.format("Current User: %s (%s)",current_user.getUsername(),current_user.getType().name()));
        UserLabel.getStyleClass().add("subheader");

        TextHeadings.getChildren().addAll(sep,Edstemus,sep2,UserLabel);
        TextHeadings.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.10));

        VBox Buttons = new VBox(10);

        Button Menu = new Button("Main Menu");
        Menu.setOnAction(event -> pane.setCenter(getMainbox()));
        Menu.prefWidthProperty().bind(primaryStage.widthProperty().multiply(buttonwidth));
        Menu.prefHeightProperty().bind(primaryStage.heightProperty().multiply(buttonheight));
        Buttons.getChildren().add(Menu);

        Button ViewScrolls = new Button("View Scrolls");
        ViewScrolls.setOnAction(event -> ScrollInteraction.addScrolls(current_user,primaryStage));
        ViewScrolls.prefWidthProperty().bind(primaryStage.widthProperty().multiply(buttonwidth));
        ViewScrolls.prefHeightProperty().bind(primaryStage.heightProperty().multiply(buttonheight));
        Buttons.getChildren().add(ViewScrolls);

        if (current_user.getType() != UserType.GUEST) {
            Button UserOptionsButton = new Button("User Options");
            UserOptionsButton.setOnAction(event -> ProfileInteraction.addUserOptions(current_user));
            UserOptionsButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(buttonwidth));
            UserOptionsButton.prefHeightProperty().bind(primaryStage.heightProperty().multiply(buttonheight));
            Buttons.getChildren().add(UserOptionsButton);
        }

        if (current_user.getType() == UserType.ADMIN){
            Button AdminOptions = new Button("Admin Options");
            AdminOptions.setOnAction(event -> AdminInteraction.addAdminTabs());
            Buttons.getChildren().addAll(AdminOptions);
            AdminOptions.prefWidthProperty().bind(primaryStage.widthProperty().multiply(buttonwidth));
            AdminOptions.prefHeightProperty().bind(primaryStage.heightProperty().multiply(buttonheight));
        }

        Buttons.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.50));

        Region spacerbottom = new Region();
        spacerbottom.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.15));

        //Panelling.setAlignment(Pos.CENTER_RIGHT);
        //Panelling.setPadding(new Insets(10, 10, 10, 10));
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> logout());

        logoutButton.prefWidthProperty().bind(primaryStage.widthProperty().multiply(buttonwidth));
        logoutButton.prefHeightProperty().bind(primaryStage.heightProperty().multiply(buttonheight));


        SideBar.getChildren().addAll(Mast,TextHeadings,Buttons,spacerbottom,logoutButton);
        SideBar.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.20));
        SideBar.prefHeightProperty().bind(primaryStage.heightProperty());

        return SideBar;
    }

    private VBox getMainbox(){

        MainBox = new VBox(10);

        MainBox.setStyle("-fx-background-color: #DCDCDC;");
        MainBox.prefWidthProperty().bind(primaryStage.widthProperty().multiply(0.80));
        MainBox.prefHeightProperty().bind(primaryStage.heightProperty().multiply(0.80));

        displayScrollOfTheDay();
        return MainBox;
    }

    public static TabPane Admintabs(){


        TabPane tabPane = new TabPane();

        users = new Tab("Users");


        VBox usercontent = AdminInteraction.getUsers();
        usercontent.setAlignment(javafx.geometry.Pos.CENTER);
        users.setContent(usercontent);

        tabPane.getTabs().add(users);

        tabPane.tabMinWidthProperty().bind(MainBox.widthProperty().divide(tabPane.getTabs().size()).subtract(20));
        tabPane.tabMaxWidthProperty().bind(MainBox.widthProperty().divide(tabPane.getTabs().size()).subtract(20));


        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.prefHeightProperty().bind(MainBox.heightProperty());


        return tabPane;
    }

    public static void refreshAdmintabs(){

        VBox usercontent = AdminInteraction.getUsers();
        usercontent.setAlignment(javafx.geometry.Pos.CENTER);
        users.setContent(usercontent);

    }

    public static void displayScrollOfTheDay(){
        ScrollData scrolldb = new ScrollData();
        Scroll scrollOfTheDay = scrolldb.getScrollOfTheDay();

        MainBox.getChildren().clear();

        if (scrollOfTheDay == null) {
            Label noScrollLabel = new Label("No scrolls available for today.");
            MainBox.getChildren().add(noScrollLabel);
        } else {
            VBox container = new VBox(10);

            Label titleLabel = new Label("Scroll of the Day: " + scrollOfTheDay.getScrollName());
            System.out.println("scroll of teh day content");
            titleLabel.getStyleClass().add("bigheader");
            TextArea contentArea = new TextArea(FileConvertor.displayScrollContent(scrollOfTheDay));
            contentArea.getStyleClass().addAll("text-area","centeredTextArea");

            contentArea.setEditable(false);
            contentArea.setWrapText(true);

            HBox scrollofday = new HBox(10);

            Region space1 = new Region();
            Region space2 = new Region();

            space1.prefWidthProperty().bind(container.widthProperty().multiply(0.20));
            space2.prefWidthProperty().bind(container.widthProperty().multiply(0.20));

            contentArea.prefHeightProperty().bind(container.heightProperty().multiply(0.60));
            contentArea.prefWidthProperty().bind(container.widthProperty().multiply(0.80));
            container.setAlignment(Pos.CENTER);

            container.prefHeightProperty().bind(MainBox.heightProperty());
            container.prefHeightProperty().bind(MainBox.widthProperty().multiply(0.80));

            scrollofday.getChildren().addAll(space1,contentArea,space2);

            container.getChildren().addAll(titleLabel, scrollofday);
            MainBox.getChildren().add(container);
        }
    }
}
