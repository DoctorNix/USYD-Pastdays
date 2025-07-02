//package CurrencyExchange; // this is use for my own as it conduct in the local root
// package soft2412.Currency; // for later use
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soft2412.Currency.Currency;
import soft2412.Currency.Rate;

import java.time.LocalDate;
import java.util.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class testCurrency {
    private Currency currency;

    @BeforeEach
    public void setUp() {
        // Initialize a Currency object before each test
        currency = new Currency("USD");
    }

    @Test
    public void testAddRate() {
        // Add a new rate EUR to check if it's added correctly
        currency.addRate("EUR", 0.85, LocalDate.of(2024, 9, 1));

        // Retrieve the latest rate
        Map<String, Rate> rates = currency.getRates();
        assertNotNull(rates.get("EUR"), "Rate for EUR should not be null");
        assertEquals(0.85, rates.get("EUR").getValue(), 0.001, "Rate value should be 0.85");
        assertEquals(LocalDate.of(2024, 9, 1), rates.get("EUR").getDate(), "Rate date should be 2024-09-01");
    }
    
    // Test for retrieving the latest rate for EUR and checking the value.
    @Test
    public void testGetLatestRate() {
        // Add a rate and test if the latest rate is correct
        currency.addRate("EUR", 1.1, LocalDate.of(2024, 9, 10));
        double LatestRate = currency.getLatestRate("EUR");

        assertEquals(1.1, LatestRate, 0.001, "Latest rate should be 1.1");
    }

    // Test for updating the rate for JPY and checking the update is reflected correctly.
    @Test
    public void testUpdateRate() {
        // Attempt to retrieve a rate for a currency that has not been added
        currency.addRate("JPY", 110, LocalDate.of(2024, 9, 10));
        currency.addRate("JPY", 115, LocalDate.of(2024, 9, 11));

        double LatestRate = currency.getLatestRate("JPY");
        assertEquals(115, LatestRate, 0.001, "Latest rate should be 115");
    }
    
    // Test for retrieving the previous rate for HKD.
    @Test
    public void testPreviousRate() {
        currency.addRate("HKD", 110, LocalDate.of(2024, 9, 10));
        currency.addRate("HKD", 115, LocalDate.of(2024, 9, 11));

        double PreviousRate = currency.getPreviousRate("HKD");
        assertEquals(110, PreviousRate, 0.001, "Previous rate should be 110");
    }
    
    // Test to check the number of rates added to the currency.
    @Test
    public void testGetRates(){
        currency.addRate("EUR", 1.1, LocalDate.of(2024, 9, 12));
        currency.addRate("GBP", 1.3, LocalDate.of(2024, 9, 10));

        Map<String, Rate> rates = currency.getRates();
        assertEquals(2, rates.size(), "There should be 2 rates in total.");
    }

    @Test
    public void testWriteRateToFile() {

        // Test that the file writing works without throwing an exception
        currency.writeRateToFile("EUR", 1.1, LocalDate.of(2024, 9, 12));

        // Since the file writing is hard to test directly, you can validate that no exceptions are thrown.
        assertDoesNotThrow(() -> currency.writeRateToFile("EUR", 1.1, LocalDate.of(2024, 9, 12)),
                "File writing should not throw an exception.");

        // Reset File after write.
        try {
            String usdPath = "src/TestDatabase/Currencies/USD.txt";
            File inputFile = new File(usdPath);
            File tempFile = new File(usdPath + ".tmp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if (!currentLine.equals("EUR,1.10,2024-09-12")) {
                    writer.write(currentLine + System.lineSeparator());
                }
            }

            writer.close();
            reader.close();

            // Delete the original file
            if (!inputFile.delete()) {
                System.err.println("Could not delete the original file");
            }

            // Rename the temp file to the original file
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Could not rename the temp file to the original file");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetCurrencySymbol() {
        // Test if the currency symbol is returned correctly
        assertEquals("USD", currency.getCurrencySymbol(), "Currency symbol should be 'USD'.");
    }

}
