package Edstemus.User;

public class User {
    private String phoneNumber;
    private String email;
    private String firstName; // For user's real name
    private String lastName;
    private String username;
    private String password;
    private final UserType type;

    public User(String phoneNumber, String email, String firstName, String lastName, String username, String password, UserType type) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.type = type;
    }
    // all the getter
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public UserType getType() {
        return type;
    }

    // all the setter
    public void setFirstName(String name) {
        this.firstName = name;
    }

    public void setLastName(String name) {
        this.lastName = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getdetails(User user){
        String details = "";

        details += ("Username: " + user.getUsername() + "\n");
        details += ("First Name: " + user.getFirstName() + "\n");
        details += ("Last Name: " + user.getLastName() + "\n");
        details += ("Email: " + user.getEmail() + "\n");
        details += ("Phone Number: " + user.getPhoneNumber() + "\n");
        details += ("Type: " + user.getType() + "\n");
        return details;
    }
}
