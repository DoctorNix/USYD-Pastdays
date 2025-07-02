package Edstemus.database;

import Edstemus.database.security.PasswordHasher;

import java.sql.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private String url = "jdbc:sqlite:src/main/resources/db/edstemus.db";
    private Connection conn;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void setDatabasePath(String dbPath) {
        url = dbPath;
    }

    public Connection connect() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url);
                System.out.println("Connected to the database!");
            }
            return conn;
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return null;
        }
    }

    public void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Users (\n"
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " phoneNumber TEXT,\n"
                + " email TEXT NOT NULL ,\n"
                + " firstName TEXT,\n"
                + " lastName TEXT,\n"
                + " username TEXT NOT NULL UNIQUE,\n"
                + " password TEXT NOT NULL,\n"
                + " userType TEXT NOT NULL CHECK (userType IN ('ADMIN', 'NORMAL'))\n"
                + ");";


        try (Connection conn = this.connect();
             Statement st = conn.createStatement()) {

            ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='Users';");
            boolean tableExists = rs.next();
            rs.close();


            if (!tableExists) {
                st.execute(sql);
                System.out.println("Users table created");
                insertAdminUser();
            } else {
                System.out.println("Table already existed, skipping user insert.");
            }

        } catch (SQLException e) {
            System.out.println("Error creating Users table: " + e.getMessage());
        }
    }

    public void insertAdminUser() {
        String hashedpass = PasswordHasher.hashPassword("password123");

        String insertAdminSql = "INSERT INTO Users (phoneNumber, email, firstName, lastName, username, password, userType) "
                + "VALUES ('041234567', 'admin@admin.com', 'admin_user', 'Smith', 'admin123', '" + hashedpass + "', 'ADMIN');";

        try (Connection conn = this.connect();
             Statement st = conn.createStatement()) {

            ResultSet adminCheck = st.executeQuery("SELECT * FROM Users WHERE username = 'admin123';");

            if (!adminCheck.next()) {
                st.execute(insertAdminSql);
                System.out.println("Default admin user inserted.");
            } else {
                System.out.println("Admin user already exists, skipping insert.");
            }
            adminCheck.close();

        } catch (SQLException e) {
            System.out.println("Error inserting admin user: " + e.getMessage());
        }
    }

    public void createScrollTable() {
        String sql = "CREATE TABLE IF NOT EXISTS Scroll (\n"
                + " ID INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + " name TEXT NOT NULL UNIQUE ,\n"
                + " content BLOB,\n"
                + " uploader_id INTEGER,\n"
                + " downloads INTEGER,\n"
                + " upload_date DATE NOT NULL,\n"
                + "password TEXT,\n"
                + " FOREIGN KEY (uploader_id) REFERENCES Users(ID) ON DELETE CASCADE\n"
                + ");";

        try (Connection conn = connect();
             Statement st = conn.createStatement()) {
            st.execute(sql);
            System.out.println("Scroll table created or already exists.");

        } catch (SQLException e) {
            System.out.println("Error creating Scroll table: " + e.getMessage());
        }
    }

    public void createScrollOfDayTable(){
        String sql = "CREATE TABLE IF NOT EXISTS ScrollOfTheDay (\n"
                + " selected_date DATE PRIMARY KEY,\n"
                + " scroll_id INT,\n"
                + " FOREIGN KEY (scroll_id) REFERENCES Scroll(ID)\n"
                + ");";

        try (Connection conn = connect();
             Statement st = conn.createStatement()) {
            st.execute(sql);
            System.out.println("ScrollOfDay table created or already exists.");

        } catch (SQLException e) {
            System.out.println("Error creating ScrollOfDay table: " + e.getMessage());
        }
    }


}
