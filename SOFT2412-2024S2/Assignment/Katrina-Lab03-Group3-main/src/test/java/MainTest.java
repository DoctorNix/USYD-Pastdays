
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soft2412.Main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

public class MainTest {

    InputStream sysInBackup;

    ByteArrayOutputStream myOut;
    PrintStream sysOutBackup;

    String extra;
    String[] arguments = {"Testing"};


    void reset_output(){
        // Create a new bytestream to capture a new output.
        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
    }

    @BeforeEach
    void startup(){
        // Capture original standard input and outputs
        sysInBackup = System.in;
        sysOutBackup = System.out;
    }

    @AfterEach
    void after(){
        // Reset original standard input and outputs
        System.setIn(sysInBackup);
        System.setOut(sysOutBackup);
    }

    @Test
    void login_options(){
        // Tests for signup, login and menu
        reset_output();

        // Test instant Exit
        System.setIn(new ByteArrayInputStream("3\n".getBytes()));
        Main.main(arguments);
        extra = "Exiting...\r\n";
        assertEquals(MenuOutputs.loaded()+MenuOutputs.menu()+extra, myOut.toString());

        reset_output();

        // Test admin login and that it shows the correct outputs.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n1\n3\n".getBytes()));
        Main.main(arguments);
        extra = MenuOutputs.loaded()+MenuOutputs.menu()+MenuOutputs.login_menu()+"Admin login successful!\r\nLogin successful!\r\n" +
                "Logged in as: admin_1\r\n"+MenuOutputs.admin_loggedin()+"Logged out successfully.\r\n"+
                MenuOutputs.menu()+"Exiting...\r\n";
        assertEquals(extra,myOut.toString());


        reset_output();
        // Test normal signup and login.
        System.setIn(new ByteArrayInputStream("1\ntest\npassword\n2\ntest\npassword\n1\n3\n".getBytes()));
        Main.main(arguments);
        extra = MenuOutputs.loaded()+MenuOutputs.menu()+MenuOutputs.signup_menu()+
                MenuOutputs.menu()+MenuOutputs.login_menu()+"Login successful!\r\n" + "Logged in as: test\r\n"+MenuOutputs.normal_loggedin()+
                "Logged out successfully.\r\n"+MenuOutputs.menu()+"Exiting...\r\n";
        assertEquals(extra,myOut.toString());

        reset_output();
        // Test for incorrect username and password.
        System.setIn(new ByteArrayInputStream("1\ntest\npassword\n2\nwrong\npassword\n2\ntest\nwrong3\n3\n".getBytes()));
        Main.main(arguments);
        extra = MenuOutputs.loaded()+MenuOutputs.menu()+MenuOutputs.signup_menu()+
                MenuOutputs.menu()+MenuOutputs.login_menu()+"User not found.\r\n"+MenuOutputs.menu()+
                MenuOutputs.login_menu()+"Incorrect password.\r\n"+
                MenuOutputs.menu()+"Exiting...\r\n";
        assertEquals(extra,myOut.toString());

    }


    // All other tests are used to test if the correct branches and functions are activated from the main menu.
    // The main functionality of these functions are tested individually.
    @Test
    void currency_conversion(){

        reset_output();

        // test to show currency conversion is reached and that a correct value is displayed.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n2\n15.00\nAUD\nUSD\n1\n3\n".getBytes()));
        Main.main(arguments);

        assertTrue(myOut.toString().contains("15.0 AUD = 9.75 USD"));

    }


    @Test
    void display_pop(){

        reset_output();

        // test to check if the conversion table is displayed.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n3\n1\n3\n".getBytes()));
        Main.main(arguments);

        assertTrue(myOut.toString().contains("|AUD         || 95     (-) || 0.61   (I) ||     --     || 0.65   (D) |"));

    }

    @Test
    void summary(){
        reset_output();

        // test to check if summary is called.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n4\nAUD\nUSD\n2023-09-14\n2024-09-14\n1\n3\n".getBytes()));
        Main.main(arguments);

        assertTrue(myOut.toString().contains("Minimum Rate: 0.65"));
    }

    @Test
    void update_pop(){
        reset_output();

        // test to check if update popular is called.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n5\n1\n3\n".getBytes()));
        Main.main(arguments);

        assertTrue(myOut.toString().contains("Which would you like to swap?:"));

    }

    @Test
    void add_new_exchange_rate(){
        reset_output();

        // test to check if adding an exchange rate is called.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n6\n1\n3\n".getBytes()));
        Main.main(arguments);

        assertTrue(myOut.toString().contains("Enter the from currency: "));
    }

    @Test
    void add_new_currency_type(){
        reset_output();

        // test to check if adding a currency is called.
        System.setIn(new ByteArrayInputStream("2\nadmin_1\nstrongPassword\n7\nAUD\nTEST\n0.01\n2024-09-14\n1\n3\n".getBytes()));
        Main.main(arguments);

        assertTrue(myOut.toString().contains("Enter conversion rate between the new currency and the initial Currency:"));
    }

}

class MenuOutputs{

    // Standard outputs for repeated sections of output statements, which are used to compare outputs.

    public static String loaded(){
        String loaded_text = "Loading currency data\r\n" +
                "Currency data loaded\r\n";
        return loaded_text;
    }
    public static String menu(){
        String menutext =
                "Choose an option:\r\n"+
                "1. Sign Up\r\n" +
                "2. Login\r\n" +
                "3. Exit\r\n"+
                "Enter choice: ";
        return menutext;
    }

    public static String signup_menu(){
        String signuptext = "Enter Username for signup: " +
                "Enter Password for signup: " +
                "User test registered successfully.\r\n";
        return signuptext;
    }

    public static String login_menu() {
        String logintext = "Enter Username for login: " +
                "Enter Password for login: ";
        return logintext;
    }

    public static String admin_loggedin(){
        String admin_text = "1. Log Out\r\n" +
                "2. Currency Conversion\r\n" +
                "3. Display Popular Currencies\r\n" +
                "4. Print Summary of Conversion Rate History\r\n" +
                "5. Update Popular Currencies\r\n" +
                "6. Add New Exchange Rate\r\n" +
                "7. Add New Currency Type\r\n" +
                "Enter choice: ";
        return admin_text;
    }

    public static String normal_loggedin(){
        String normal_text = "1. Log Out\r\n" +
                "2. Currency Conversion\r\n" +
                "3. Display Popular Currencies\r\n" +
                "4. Print Summary of Conversion Rate History\r\n" +
                "Enter choice: ";
        return normal_text;
    }

}