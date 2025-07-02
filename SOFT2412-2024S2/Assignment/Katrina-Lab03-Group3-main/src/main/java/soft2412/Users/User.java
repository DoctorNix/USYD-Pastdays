package soft2412.Users;

public class User {
    private String username;
    private UserRole role;

    public User(String username, UserRole inputRole) {
        this.username = username;
        this.role = inputRole;
    }

    public String getUsername(){
        return this.username;
    }

    public UserRole getRole(){
        return this.role;
    }

    public boolean isAdmin(){
        return this.role == UserRole.ADMIN;
    }

    public static User createAdmin(String username){
        return new User(username, UserRole.ADMIN);
    }

    public static User createNormal(String username){
        return new User(username, UserRole.NORMAL);
    }

}
