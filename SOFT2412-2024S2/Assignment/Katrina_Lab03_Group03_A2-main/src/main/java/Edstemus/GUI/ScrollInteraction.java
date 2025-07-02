package Edstemus.GUI;

import Edstemus.Scroll.FileConvertor;
import Edstemus.Scroll.Scroll;
import Edstemus.Scroll.ScrollSearchOptions;
import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.database.ScrollData;
import Edstemus.database.UserData;
import Edstemus.view.DashboardScene;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import javafx.scene.control.Alert.AlertType;
import Edstemus.database.security.PasswordHasher;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ScrollInteraction {

    private static TextField scrollnameField;
    private static TextField scrollpathField;
    private static TextField scrollpwdField;

    private static TextField accessScrollPassField;

    private static TextField searchScrollNameField;
    private static TextField scrollIDField;
    private static TextField ownerIDField;
    private static TextField minDownloadsField;
    private static TextField maxDownloadsField;
    private static DatePicker uploadDateFromField;
    private static DatePicker uploadDateToField;
    private static TextArea editable_area;

    private static Scroll current = null;

    private static HBox buttons = null;
    private static User current_user = null;

    private static String chosen_file = null;

    public static void addScrolls(User current_user, Stage primaryStage){
        MainBoxMethods.clearMain();

        VBox scrolls = getScrollbox(current_user, primaryStage);
        scrolls.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty());
        DashboardScene.MainBox.getChildren().add(scrolls);
    }

    private static VBox getScrollbox(User user, Stage primaryStage){

        current_user = user;

        ScrollPane overall = new ScrollPane();

        VBox container = new VBox(10);

        HBox addScrolls = new HBox(10);

        scrollnameField = new TextField();
        scrollnameField.setPromptText("Scroll Name*");

        //scrollpathField = new TextField();
        //scrollpathField.setPromptText("Scroll FILE PATH");

        FileChooser scrollField = new FileChooser();
        Button scrollpathField = new Button("Scroll Path");
        scrollpathField.setOnAction(e -> {
             chosen_file = (scrollField.showOpenDialog(primaryStage)).getAbsolutePath();
        });

        scrollpwdField = new TextField();
        scrollpwdField.setPromptText("Scroll PASSWORD");


        HBox textbuttonbox = new HBox(10);
        getTextButton(current_user,textbuttonbox);
        buttons = textbuttonbox;
        textbuttonbox.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty().multiply(0.1));


        VBox currentScrolls = new VBox(10);
        populatescrolls(currentScrolls);

        if (current_user.getType() != UserType.GUEST) {
            Label addScrollLabel = new Label("Add a Scroll (* = required field)");
            addScrollLabel.getStyleClass().add("subheader");
            Button addScroll = new Button("Add Scroll");
            addScroll.setOnAction(event -> addScroll(currentScrolls));

            addScrolls.getChildren().addAll(scrollnameField, scrollpathField, scrollpwdField, addScroll);
            container.getChildren().addAll(addScrollLabel, addScrolls);

            addScrolls.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty().multiply(0.05));
        }


        VBox searchBox = getSearchBox(currentScrolls);

        ScrollPane scrollablecurrent = new ScrollPane();
        scrollablecurrent.setContent(currentScrolls);
        scrollablecurrent.setFitToWidth(true);

        HBox container2 = new HBox(10);
        // Add sections to container
        container2.getChildren().addAll(
                searchBox,
                scrollablecurrent
        );

        container2.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty().multiply(0.35));


        editable_area = getTextField();
        editable_area.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty().multiply(0.5));


        Label previeweditLabel = new Label("View/Edit a Scroll");
        previeweditLabel.getStyleClass().add("subheader");
        container.getChildren().addAll(container2,previeweditLabel,editable_area,textbuttonbox);

        //overall.setContent(container);
        return container;
    }

    private static String inputPassword(){

        Stage inputStage = new Stage();
        inputStage.initModality(Modality.APPLICATION_MODAL);
        inputStage.setTitle("Enter Password");
        accessScrollPassField = new TextField();
        accessScrollPassField.setPromptText("Enter Password");

        Button submit = new Button("Submit");
        submit.setOnAction(event -> inputStage.close());

        accessScrollPassField.setOnAction(event ->{inputStage.close();});

        VBox passwordLayout = new VBox(20);
        passwordLayout.getChildren().addAll(accessScrollPassField, submit);
        passwordLayout.getChildren().addAll(new Label("This scroll is password protected. Enter scroll password"));
        Scene passwordScene = new Scene(passwordLayout, 300, 200);
        inputStage.setScene(passwordScene);
        inputStage.showAndWait();

        return accessScrollPassField.getText();
    }

    private static void addScroll(VBox currentScrolls){
        String path = "";
        if (chosen_file != null){
            path = chosen_file;
        }

        String name = scrollnameField.getText();
        String password = scrollpwdField.getText();

        System.out.println("PATH"+ path);
        //scrollpathField.setText("");
        scrollnameField.setText("");
        scrollpwdField.setText("");
        chosen_file = null;

        ScrollData scrolldb = new ScrollData();
        UserData userdb = new UserData();

        Scroll temp;
        if(name.isEmpty()){
            Alert a =  new Alert(AlertType.ERROR);
            a.setContentText("Error: Scroll name is required");
            a.show();
            return;
        }
        if (path.isEmpty()){
            byte[] content = new byte[0];
            temp = new Scroll(userdb.getIdFromUser(current_user.getUsername()),name,content,0,LocalDate.now());
        }
        else{
            temp = FileConvertor.saveContentToScroll(path,userdb.getIdFromUser(current_user.getUsername()),name,0);
        }

        if (!password.isEmpty()) {
            System.out.println("Original password: " + password);
            temp.setPassword(password);
        }

        if (temp != null){
            scrolldb.insertScroll(temp);
            currentScrolls.getChildren().clear();
            populatescrolls(currentScrolls);
        }
        currentScrolls.getChildren().clear();
        populatescrolls(currentScrolls);

    }

    static void populatescrolls(VBox container){
        ScrollData scrolldb = new ScrollData();
        ArrayList<String> currentscrolls = scrolldb.displayAllScrolls();
        Collections.reverse(currentscrolls);

        Label currentScrollslabel = new Label("Current Scrolls");
        currentScrollslabel.getStyleClass().add("subheader");
        container.getChildren().add(currentScrollslabel);

        for (String scroll : scrolldb.displayAllScrolls()){
            container.getChildren().add(createScrollBox(scrolldb.getScroll(scroll),scrolldb,container));
        }
        container.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.5));
    }



    public static ScrollPane getScrolls(){
        ScrollPane pane = new ScrollPane();

        VBox container = new VBox(10);

        populatescrolls(container);

        pane.setContent(container);

        return pane;
    }

    private static HBox createScrollBox(Scroll given_scroll, ScrollData scrolldb, VBox scroll_container){
        HBox temp = new HBox();
        Button scroll = new Button("ID: " + given_scroll.getScrollID() + " - " + given_scroll.getScrollName());
        temp.getChildren().add(scroll);
        scroll.setAlignment(Pos.BASELINE_LEFT);

        scroll.prefWidthProperty().bind(temp.widthProperty().multiply(0.5));

        Button preview = new Button("Select");
        preview.setOnAction(event -> getScrollContent(given_scroll));
        temp.getChildren().add(preview);

        UserData userdb = new UserData();
        if (current_user != null){
            if (given_scroll.getOwnerID() == userdb.getIdFromUser(current_user.getUsername())){
                Button delete = new Button("Delete");
                delete.setOnAction(event -> deleteScrollBox(scroll_container,given_scroll));
                temp.getChildren().add(delete);
                preview.prefWidthProperty().bind(temp.widthProperty().multiply(0.25));
                delete.prefWidthProperty().bind(temp.widthProperty().multiply(0.25));
            }
            else{
                preview.prefWidthProperty().bind(temp.widthProperty().multiply(0.5));
            }
        }
        return temp;
    }

    private static TextArea getTextField(){
        TextArea editable_area = new TextArea();
        editable_area.setPromptText("Click on a scroll to edit or view");
        editable_area.setEditable(false);

        editable_area.prefHeightProperty().bind(DashboardScene.MainBox.heightProperty().multiply(0.5));
        return editable_area;
    }

    private static void getTextButton(User current_user, HBox buttons){
        UserData userdb = new UserData();

        if (current_user.getType() != UserType.GUEST) {
            Button download = new Button("Download");
            download.setOnAction(event -> downloadScroll());

            if (current != null && current.getOwnerID() == userdb.getIdFromUser(current_user.getUsername())){
                Button edit = new Button();
                edit.setOnAction(event -> toggleedits(edit));
                if (editable_area.isEditable()){
                    edit.setText("Editing/Previewing : Editing");
                }
                else{
                    edit.setText("Editing/Previewing : Previewing");
                }


                Button reset = new Button("Reset");
                reset.setOnAction(event -> cleartext(buttons,current_user));

                Button update = new Button("Update");
                update.setOnAction(event -> updateText());

                buttons.getChildren().addAll(edit,reset,update);
                edit.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.24));
                reset.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.24));
                update.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.24));
                download.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.24));
            }
            else{
                download.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty());
            }
            buttons.getChildren().add(download);

            }

    }

    private static void toggleedits(Button edit){

        if (editable_area.isEditable()){
            edit.setText("Editing/Previewing : Previewing");
            editable_area.setEditable(false);
        }
        else{
            edit.setText("Editing/Previewing : Editing");
            editable_area.setEditable(true);
        }
    }

    private static void cleartext(HBox buttons, User current_user){
        //field.setText("");
        if (current != null){
            getScrollContent(current);
        }
    }

    private static void getScrollContent(Scroll scroll){
        System.out.println("password: " + scroll.getPassword());
        System.out.println("Is password null? " + (scroll.getPassword() == null));
        System.out.println("Does scroll need a password? " + scroll.doesScrollNeedPassword());

        if(scroll.doesScrollNeedPassword()){
            String enteredPass = inputPassword();
            System.out.println("entered pass: " + enteredPass);
            accessScrollPassField.setText("");

            if(!PasswordHasher.verifyPassword(enteredPass, scroll.getPassword())){
                Alert a =  new Alert(AlertType.ERROR);
                a.setContentText("Error: Incorrect password");
                a.show();
                return;
            }
        }
        current = scroll;

        String data = FileConvertor.displayScrollContent(scroll);
        if (data == null){
            editable_area.setText("");
            editable_area.setPromptText("No content available in this scroll.");
        }
        else{
            editable_area.setText(data);
        }

        if (buttons != null && current_user != null){
            buttons.getChildren().clear();
            getTextButton(current_user, buttons);
        }
    }

    private static void updateText(){
        String content = editable_area.getText();
        ScrollData db = new ScrollData();

        if (current != null){
            Scroll updatedScroll = db.updateScrollData(content.getBytes(), current);
            if (updatedScroll != null){
                current = updatedScroll;
                System.out.println("Scroll Updated content: " + new String(current.getScrollData()));
                VBox scrollBox = (VBox) editable_area.getParent().lookup(".vbox");
                if (scrollBox != null) {
                    scrollBox.getChildren().clear();
                    populatescrolls(scrollBox);
                }
                editable_area.setText(new String(current.getScrollData()));
            }

            else{
                System.out.println("Scroll not updated");
            }
        }
        else{
            System.out.println("No scroll selected");
        }
    }

    private static void downloadScroll(){

        if (current != null){
            String downloadPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator + current.getScrollName();
            FileConvertor.convertTxtToBin(current.getScrollData(), downloadPath);
            System.out.println("Scroll Downloaded");

            ScrollData scrolldb = new ScrollData();
            scrolldb.increaseDownload(current.getScrollID());
        }
    }

    private static void searchScrollsOption(VBox scrollbox) {
        String scrollName = searchScrollNameField.getText().trim();
        String scrollIDText = scrollIDField.getText().trim();
        String ownerIDText = ownerIDField.getText().trim();
        String minDownloadsText = minDownloadsField.getText().trim();
        String maxDownloadsText = maxDownloadsField.getText().trim();
        LocalDate uploadDateFrom = uploadDateFromField.getValue();
        LocalDate uploadDateTo = uploadDateToField.getValue();

        ScrollData scrolldb = new ScrollData();
        ScrollSearchOptions criteria = new ScrollSearchOptions();

        if (!scrollName.isEmpty()) {
            criteria.addCriteria(SearchField.SCROLL_NAME, scrollName);
        }
        if (!scrollIDText.isEmpty()) {
            try {
                int scrollID = Integer.parseInt(scrollIDText);
                criteria.addCriteria(SearchField.SCROLL_ID, scrollID);
            } catch (NumberFormatException e) {
                System.out.println("Invalid scroll ID format");
            }
        }
        if (!ownerIDText.isEmpty()) {
            try {
                int ownerID = Integer.parseInt(ownerIDText);
                criteria.addCriteria(SearchField.OWNER_ID, ownerID);
            } catch (NumberFormatException e) {
                System.out.println("Invalid owner ID format");
            }
        }
        if (!minDownloadsText.isEmpty()) {
            try {
                int minDownloads = Integer.parseInt(minDownloadsText);
                criteria.addCriteria(SearchField.MIN_DOWNLOADS, minDownloads);
            } catch (NumberFormatException e) {
                System.out.println("Invalid minimum downloads format");
            }
        }
        if (!maxDownloadsText.isEmpty()) {
            try {
                int maxDownloads = Integer.parseInt(maxDownloadsText);
                criteria.addCriteria(SearchField.MAX_DOWNLOADS, maxDownloads);
            } catch (NumberFormatException e) {
                System.out.println("Invalid maximum downloads format");
            }
        }
        if (uploadDateFrom != null) {
            criteria.addCriteria(SearchField.UPLOAD_DATE_FROM, uploadDateFrom);
        }
        if (uploadDateTo != null) {
            criteria.addCriteria(SearchField.UPLOAD_DATE_TO, uploadDateTo);
        }

        List<Scroll> searchResults = scrolldb.searchScrolls(criteria);
        scrollbox.getChildren().clear();

        scrollbox.getChildren().add(new Label("Current Scrolls"));
        if (searchResults.isEmpty()) {
            scrollbox.getChildren().add(new Label("No results found."));
        } else {
            for (Scroll scroll : searchResults) {
                if (current_user != null){
                    scrollbox.getChildren().add(createScrollBox(scroll, scrolldb,scrollbox));
                }
            }
        }
    }

    private static VBox getSearchBox(VBox scrollBox){

        VBox searchBox = new VBox(10);

        HBox row1 = new HBox();
        searchScrollNameField = new TextField();
        searchScrollNameField.setPromptText("Search Scroll Name");

        searchScrollNameField.prefWidthProperty().bind(row1.widthProperty());
        row1.getChildren().add(searchScrollNameField);

        HBox row2 = new HBox();
        scrollIDField = new TextField();
        scrollIDField.setPromptText("Scroll ID");
        ownerIDField = new TextField();
        ownerIDField.setPromptText("Owner ID");

        scrollIDField.prefWidthProperty().bind(row2.widthProperty().multiply(0.5));
        ownerIDField.prefWidthProperty().bind(row2.widthProperty().multiply(0.5));
        row2.getChildren().addAll(scrollIDField,ownerIDField);

        HBox row3 = new HBox();
        minDownloadsField = new TextField();
        minDownloadsField.setPromptText("Min Downloads");
        maxDownloadsField = new TextField();
        maxDownloadsField.setPromptText("Max Downloads");

        minDownloadsField.prefWidthProperty().bind(row3.widthProperty().multiply(0.5));
        maxDownloadsField.prefWidthProperty().bind(row3.widthProperty().multiply(0.5));
        row3.getChildren().addAll(minDownloadsField,maxDownloadsField);


        HBox row4 = new HBox();
        uploadDateFromField = new DatePicker();
        uploadDateFromField.setPromptText("Upload Date From");
        uploadDateToField = new DatePicker();
        uploadDateToField.setPromptText("Upload Date To");

        uploadDateFromField.prefWidthProperty().bind(row4.widthProperty().multiply(0.5));
        uploadDateToField.prefWidthProperty().bind(row4.widthProperty().multiply(0.5));
        row4.getChildren().addAll(uploadDateFromField,uploadDateToField);

        HBox row5 = new HBox();
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> searchScrollsOption(scrollBox));

        searchButton.prefWidthProperty().bind(row5.widthProperty());
        row5.getChildren().addAll(searchButton);

        Label searchScrolllabel = new Label("Search Scrolls");
        searchScrolllabel.getStyleClass().add("subheader");
        searchBox.getChildren().addAll(searchScrolllabel,row1,row2,row3,row4,row5);

        searchBox.prefWidthProperty().bind(DashboardScene.MainBox.widthProperty().multiply(0.5));

        row1.prefWidthProperty().bind(searchBox.widthProperty().subtract(5));
        row2.prefWidthProperty().bind(searchBox.widthProperty().subtract(5));
        row3.prefWidthProperty().bind(searchBox.widthProperty().subtract(5));
        row4.prefWidthProperty().bind(searchBox.widthProperty().subtract(5));
        row5.prefWidthProperty().bind(searchBox.widthProperty().subtract(5));


        return searchBox;
    }

    public static void deleteScrollBox(VBox container, Scroll selectedScroll){
        ScrollData scrolldb = new ScrollData();
        UserData userdb = new UserData();
        // only the owner can delete their own scroll for now
        if (selectedScroll.getOwnerID() == userdb.getIdFromUser(current_user.getUsername())) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Scroll");
            alert.setHeaderText("Are you sure you want to delete this scroll?");
            alert.setContentText("Scroll: "+selectedScroll.getScrollName());
            // Final Confirmation
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                // start doing delete
                boolean isDeleted = scrolldb.deleteScroll(selectedScroll.getScrollID(),selectedScroll.getOwnerID());
                if (isDeleted){
                    container.getChildren().clear();
                    populatescrolls(container);
                    System.out.println("Scroll" + selectedScroll.getScrollName() + "Deleted Successfully");
                } else {
                    System.out.println("Scroll" + selectedScroll.getScrollName() + "Is Failed To Delete");
                }
            }
        } else {
            System.out.println("You are not authorized to delete this scroll");
        }
    }
}
