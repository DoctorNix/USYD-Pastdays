package Edstemus.database;

import Edstemus.User.User;
import Edstemus.User.UserType;
import Edstemus.database.security.PasswordHasher;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UserData {
    private DatabaseManager dbManager;
    private HashMap<String, User> nameToUser = new HashMap<>();

    public UserData(){
        dbManager = DatabaseManager.getInstance();
        loadUsersFromDatabase();
    }

    public boolean insertUser(User user) {
        // the ? are placeholders for values which will be provided later
        String sql = "INSERT INTO Users(phoneNumber, email, firstName, lastName, username, password, userType) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getPhoneNumber());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getUsername());
            pstmt.setString(6, PasswordHasher.hashPassword(user.getPassword()));
            pstmt.setString(7, user.getType().name());

            int rowsInserted = pstmt.executeUpdate();
            System.out.println("User inserted: " + user.getUsername());
            loadUsersFromDatabase();
            return rowsInserted > 0;


        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
            return false;
        }
    }

    public String getPasswordForUser(String username) {
//        String sql = "SELECT password FROM Users WHERE username = ?";
//
//        try (Connection conn = dbManager.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setString(1, username);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    return rs.getString("password");
//                } else {
//                    System.out.println("User " + username + " not found");
//                    return null;
//                }
//            }
//        } catch (SQLException e) {
//
//            e.printStackTrace();
//            return null;
//        }
        User targetUser = nameToUser.get(username);
        if (targetUser == null) {
            System.out.println("User not found: " + username);
            return null;
        }
        return targetUser.getPassword();
    }

    public boolean deleteUser(User user) {
        String sql = "DELETE FROM Users WHERE username = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, user.getUsername());

            int rowsDeleted = pstmt.executeUpdate();

            if (rowsDeleted == 0) {
                System.out.println("User " + user.getUsername() + " not found");
                return false;
            } else {
                nameToUser.remove(user.getUsername());
                System.out.println("User " + user.getUsername() + " has been deleted.");
                return true;
            }
        } catch (SQLException e){
            System.out.println("Error in deleting user " + e.getMessage());
            return false;
        }
    }

    private User createUserFromSql(ResultSet rs){
        try{
            String phoneNumber = rs.getString("phoneNumber");
            String email = rs.getString("email");
            String firstname = rs.getString("firstName");
            String lastname = rs.getString("lastName");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String role = rs.getString("userType");


            Edstemus.User.UserType currentType;

            if(role.equals("NORMAL")) {
                currentType = UserType.NORMAL;
            } else {
                currentType = UserType.ADMIN;
            }

            return new User(phoneNumber, email, firstname, lastname, username,password, currentType);

        }
        catch(SQLException e){
            return null;
        }
    }

    public User getUser(String username){
//        String sql = "SELECT * FROM Users WHERE username = ?";
//        User current_user = null;
//
//        try (Connection conn = dbManager.connect();
//             PreparedStatement pstmt = conn.prepareStatement(sql)){
//
//            pstmt.setString(1, username);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    current_user = createUserFromSql(rs);
//                } else {
//                    System.out.println("User " + username + " not found.");
//                }
//            }
//        }
//        catch(SQLException e){
//            System.out.println(e);
//        }
//        return current_user;
        User targetUser = nameToUser.get(username);
        if (targetUser == null) {
            System.out.println("User not found: " + username);
        }
        return targetUser;
    }

    public ArrayList<String> displayAllUsers() {
        loadUsersFromDatabase();

        ArrayList<String> allUsers = new ArrayList<>();
        for (String name : nameToUser.keySet()) {
            allUsers.add(name);
        }
        return allUsers;
    }


    public void loadUsersFromDatabase(){    //run on startup to get all users

        String sql = "SELECT * FROM Users";

        try{
            Connection conn = dbManager.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                User current_user = createUserFromSql(rs);
                if (current_user.equals(null)) {
                    throw new SQLException("Error in retrieving user");
                }
                nameToUser.put(current_user.getUsername(), current_user);
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }


    }

    public void updateUserField(String username, String fieldUpdate, String newValue){
        if (!isFieldValid(fieldUpdate)) {
            System.out.println("Invalid field name provided: " + fieldUpdate);
            return;
        }

        if ("password".equals(fieldUpdate)) {
            newValue = PasswordHasher.hashPassword(newValue);
        }

        String updateQuery = "UPDATE Users SET " + fieldUpdate + " = ? WHERE username = ?";

        try (Connection connection = dbManager.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, username);

            int rowsUpdated = preparedStatement.executeUpdate();

            // update nameToUser
            if (rowsUpdated > 0) {
                System.out.println(fieldUpdate + " updated successfully for user: " + username);

                User targetUser = nameToUser.get(username);
                if (targetUser != null) {

                    switch (fieldUpdate) {
                        case "phoneNumber" -> targetUser.setPhoneNumber(newValue);
                        case "email" -> targetUser.setEmail(newValue);
                        case "firstName" -> targetUser.setFirstName(newValue);
                        case "lastName" -> targetUser.setLastName(newValue);
                        case "username" -> {
                            nameToUser.remove(username);
                            targetUser.setUsername(newValue);
                            nameToUser.put(newValue, targetUser);
                        }
                        case "password" -> targetUser.setPassword(newValue);
                    }
                }

            } else {
                System.out.println("User not found or field unchanged.");
            }

        } catch (SQLException e) {
            System.out.println("Error updating " + fieldUpdate + ": " + e.getMessage());
        }
    }

    private boolean isFieldValid(String fieldName) {
        return switch (fieldName) {
            case "phoneNumber", "email", "firstName", "lastName", "username", "password" -> true;
            default -> false;
        };
    }

    public int getIdFromUser(String username){
        String sql = "SELECT ID FROM Users WHERE username = ?";
        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1,username);

            ResultSet results = pstmt.executeQuery();
            while(results.next()){
                return results.getInt("ID");
            }
            return 0;

        } catch (SQLException e) {
            System.out.println("Error getting userid: " + e.getMessage());
            return 0;
        }
    }

}
