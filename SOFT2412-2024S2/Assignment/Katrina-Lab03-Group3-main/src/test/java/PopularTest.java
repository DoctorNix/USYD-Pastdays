import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import soft2412.Currency.CurrencyManager;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.ByteArrayInputStream;


public class PopularTest {

    CurrencyManager testcurrencymanager  = new CurrencyManager();

    InputStream sysInBackup;
    @BeforeEach
    void set_currency(){
        // Sets the currencymanager path to the test database.
        testcurrencymanager.setpaths("src/TestDatabase/Currencies","Popular.txt");
        testcurrencymanager.loadCurrencies();
        sysInBackup= System.in;
    }

    @AfterEach
    void reset_popular(){
        // resets the currencies to default values.
        ArrayList<String> reset_currencies = new ArrayList<>();
        reset_currencies.add("CNY");
        reset_currencies.add("JPY");
        reset_currencies.add("EUR");
        reset_currencies.add("AUD");

        System.setIn(new ByteArrayInputStream("1\nUSD\n".getBytes()));
        testcurrencymanager.popularCurrencies = reset_currencies;
        testcurrencymanager.setPopularCurrencies();


        System.setIn(sysInBackup);
    }

    @Test
    void test_loading_popular(){

        // Tests if the popular currency array is correctly loaded in.
        ArrayList<String> test_currencies = new ArrayList<>();
        test_currencies.add("JPY");
        test_currencies.add("EUR");
        test_currencies.add("AUD");
        test_currencies.add("USD");

        assertEquals(test_currencies, testcurrencymanager.popularCurrencies);

    }

    @Test
    void test_swap(){

        //  Tests that the correct currency is swapped.
        System.setIn(new ByteArrayInputStream("1\nCNY\n".getBytes()));

        testcurrencymanager.setPopularCurrencies();

        ArrayList<String> test_currencies = new ArrayList<>();
        test_currencies.add("EUR");
        test_currencies.add("AUD");
        test_currencies.add("USD");
        test_currencies.add("CNY");

        assertEquals(test_currencies, testcurrencymanager.popularCurrencies);

    }

    @Test
    void test_incorrect_swap_no(){

        // Tests if the incorrect number was chosen if it was swapped or not.
        ArrayList<String> test_currencies = new ArrayList<>();
        test_currencies.add("JPY");
        test_currencies.add("EUR");
        test_currencies.add("AUD");
        test_currencies.add("USD");

        System.setIn(new ByteArrayInputStream("5\nAUD\n".getBytes()));
        testcurrencymanager.setPopularCurrencies();
        assertEquals(test_currencies, testcurrencymanager.popularCurrencies);

        System.setIn(new ByteArrayInputStream("0\nAUD\n".getBytes()));
        testcurrencymanager.setPopularCurrencies();
        assertEquals(test_currencies, testcurrencymanager.popularCurrencies);

    }

    @Test
    void test_incorrect_currency(){

        //checks if the inccorect currency was selected to be swapped.
        ArrayList<String> test_currencies = new ArrayList<>();
        test_currencies.add("JPY");
        test_currencies.add("EUR");
        test_currencies.add("AUD");
        test_currencies.add("USD");

        System.setIn(new ByteArrayInputStream("3\naud\n".getBytes()));
        testcurrencymanager.setPopularCurrencies();
        assertEquals(test_currencies, testcurrencymanager.popularCurrencies);

        System.setIn(new ByteArrayInputStream("3\nCYN\n".getBytes()));
        testcurrencymanager.setPopularCurrencies();
        assertEquals(test_currencies, testcurrencymanager.popularCurrencies);

    }


}
