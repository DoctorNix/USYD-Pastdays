

import soft2412.Currency.CurrencyManager;
import soft2412.Displayer.ConversionTable;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class DisplayTest {

    CurrencyManager testcurrencymanager;
    ConversionTable test;

    String cell_value;
    String diagonal = "     --     ";
    PrintStream sysOutBackup;
    ByteArrayOutputStream myOut;

    void settestmanagers(String test_path,String pop_path){
        // Method to change the currency manager's paths to specified directories.
        testcurrencymanager = new CurrencyManager();
        testcurrencymanager.setpaths(test_path,pop_path);
        testcurrencymanager.loadCurrencies();


        test = new ConversionTable(testcurrencymanager);
        test.refresh_titles();
        test.update_values();
    }

    // The following tests check the values within currency manager.
    @Test
    void test_no_currencies(){
        // test if no currency documents in specified directory.
        settestmanagers("src/TestDatabase/EmptyDir","test.txt");

        cell_value = test.get_cell_value(0, 1);
        assertEquals(null,cell_value);

        cell_value = test.get_cell_value(1, 0);
        assertEquals(null,cell_value);


        cell_value = test.get_cell_value(1, 1);
        assertEquals(diagonal,cell_value);

        cell_value = test.get_cell_value(1, 2);
        assertEquals(diagonal,cell_value);

    }

    @Test
    void test_no_values(){
        // Test to see when there are files but no entries.
        settestmanagers("src/TestDatabase/EmptyCurrencies","Popular.txt");

        // Loads correct Names
        cell_value = test.get_cell_value(0,3);
        assertEquals("EUR",cell_value);

        cell_value = test.get_cell_value(2, 0);
        assertEquals("KRW", cell_value);

        // Loads a place holder in null cells.
        cell_value = test.get_cell_value(2,2);
        assertEquals(diagonal,cell_value);

        cell_value = test.get_cell_value(2,3);
        assertEquals(diagonal,cell_value);
    }

    @Test
    void test_loaded_currencies(){
        // test for valid files with valid currencies.
        // This also tests for increase, decrease and no change between two rates.
        settestmanagers("src/TestDatabase/Currencies","Popular.txt");

        cell_value = test.get_cell_value(1,3);
        assertEquals(" 95     (-) ",cell_value);

        cell_value = test.get_cell_value(2,3);
        assertEquals(" 0.61   (I) ",cell_value);

        cell_value = test.get_cell_value(3,3);
        assertEquals(diagonal,cell_value);

        cell_value = test.get_cell_value(4,3);
        assertEquals(" 0.65   (D) ",cell_value);

    }

    // The following Tests check for the correct std output.

    @Test
    void test_empty_display(){

        // Tests if what is displayed when an empty directory is given.
        sysOutBackup = System.out;
        settestmanagers("src/TestDatabase/EmptyDir","Popular.txt");

        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        test.showDisplay();

        String standardOutput = myOut.toString();

        assertNotNull(standardOutput);
        assertEquals(ExampleOutputs.blank_conversion(),standardOutput);
        //assertArrayEquals(ExampleOutputs.blank_conversion().getBytes(), standardOutput.getBytes());

        System.out.flush();

        System.setOut(sysOutBackup);
    }

    @Test
    void test_full_display(){
        // tests to display with a valid directory.
        sysOutBackup = System.out;
        settestmanagers("src/TestDatabase/Currencies","Popular.txt");

        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        test.showDisplay();

        String standardOutput = myOut.toString();

        assertNotNull(standardOutput);
        assertEquals(ExampleOutputs.normal_conversion(),standardOutput);
        //assertArrayEquals(ExampleOutputs.normal_conversion().getBytes(), standardOutput.getBytes());

        System.out.flush();

        System.setOut(sysOutBackup);
    }

    @Test
    void test_swap_display(){
        // Tests correct output after swapping popular currency.
        sysOutBackup = System.out;
        settestmanagers("src/TestDatabase/Currencies","Popular.txt");

        myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));

        InputStream sysInBackup = System.in;

        System.setIn(new ByteArrayInputStream("2\nCNY\n".getBytes()));
        testcurrencymanager.setPopularCurrencies();

        test = new ConversionTable(testcurrencymanager);
        test.refresh_titles();
        test.update_values();

        test.showDisplay();

        String standardOutput = myOut.toString();

        assertNotNull(standardOutput);
        assertEquals(ExampleOutputs.swap_conversion(),standardOutput);
        //assertArrayEquals(ExampleOutputs.normal_conversion().getBytes(), standardOutput.getBytes());

        ArrayList<String> reset_currencies = new ArrayList<>();
        reset_currencies.add("CNY");
        reset_currencies.add("JPY");
        reset_currencies.add("EUR");
        reset_currencies.add("AUD");

        System.setIn(new ByteArrayInputStream("1\nUSD\n".getBytes()));
        testcurrencymanager.popularCurrencies = reset_currencies;
        testcurrencymanager.setPopularCurrencies();

        System.setOut(sysOutBackup);
        System.setIn(sysInBackup);
    }
}

class ExampleOutputs {

    // Standard outputs for tables, which are used to compare outputs.

    public static String blank_conversion(){
        String table ="\nConversion Table\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|From/To     ||            ||            ||            ||            |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|            ||     --     ||     --     ||     --     ||     --     |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|            ||     --     ||     --     ||     --     ||     --     |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|            ||     --     ||     --     ||     --     ||     --     |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|            ||     --     ||     --     ||     --     ||     --     |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "All values capped at format ####.####\r\n";
        return table;
    }

    public static String normal_conversion(){
        String table ="\nConversion Table\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|From/To     ||JPY         ||EUR         ||AUD         ||USD         |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|JPY         ||     --     || 0.0063 (-) || 0.01   (-) || 0.007  (-) |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|EUR         || 158    (-) ||     --     || 1.66   (-) || 1.11   (-) |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|AUD         || 95     (-) || 0.61   (I) ||     --     || 0.65   (D) |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|USD         || 143    (-) || 0.09   (-) || 1.5    (-) ||     --     |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "All values capped at format ####.####\r\n";
        return table;
    }

    public static String swap_conversion() {
        String table = "Which would you like to swap?: \r\n" +
                "1.JPY\r\n" +
                "2.EUR\r\n" +
                "3.AUD\r\n" +
                "4.USD\r\n" +
                "5.Exit\r\n" +
                "What would you like to replace it with?: \r\n" +
                "GBP\r\n" +
                "CNY\r\n" +
                "\nConversion Table\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|From/To     ||JPY         ||AUD         ||USD         ||CNY         |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|JPY         ||     --     || 0.01   (-) || 0.007  (-) || 0.05   (-) |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|AUD         || 95     (-) ||     --     || 0.65   (D) || 4.75   (-) |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|USD         || 143    (-) || 1.5    (-) ||     --     || 7.11   (-) |\r\n" +
                "----------------------------------------------------------------------\r\n" +
                "|CNY         || 20     (-) || 0.21   (-) || 0.14   (-) ||     --     |\r\n" +
                "----------------------------------------------------------------------\r\n"+
                "All values capped at format ####.####\r\n";
        return table;
    }
}
