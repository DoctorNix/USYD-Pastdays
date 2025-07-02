import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soft2412.Currency.CurrencyManager;
import soft2412.Currency.Currency;
import javax.management.InstanceNotFoundException;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class testCurrencyManager {
    private CurrencyManager currencyManager;
    private final InputStream originalIn = System.in; // using to simulate user input

    // Initializes the CurrencyManager and loads currencies from test database (instead of the main database) before each test.
    @BeforeEach
    public void setUp() {
        currencyManager = new CurrencyManager();
        currencyManager.setpaths("src/TestDatabase/Currencies","Popular.txt");
        currencyManager.loadCurrencies();
    }

    // Use tearDown method to clean up after each test and reset setIn.
    @AfterEach
    public void tearDown() {
        System.setIn(originalIn);
        deleteFile("src/TestDatabase/Currencies/CAD.txt");
        removeLineFromFile("src/TestDatabase/Currencies/USD.txt", "EUR,0.85,2024-09-12");
        removeLineFromFile("src/TestDatabase/Currencies/USD.txt", "EUR,1.25,2024-09-15");
        removeLineFromFile("src/TestDatabase/Currencies/USD.txt", "EUR,0.00,2024-09-15");
        removeLineFromFile("src/TestDatabase/Currencies/USD.txt", "USD,1.25,2024-09-15");
        removeLineFromFile("src/TestDatabase/Currencies/Popular.txt", "CAD");
        removeLineContaining("src/TestDatabase/Currencies/USD.txt", "CAD");
        removeLineContaining("src/TestDatabase/Currencies/EUR.txt", "CAD");
        removeLineContaining("src/TestDatabase/Currencies/JPY.txt", "CAD");
        removeLineContaining("src/TestDatabase/Currencies/CNY.txt", "CAD");
        removeLineContaining("src/TestDatabase/Currencies/AUD.txt", "CAD");
        removeLineContaining("src/TestDatabase/Currencies/GBP.txt", "CAD");
    }

    // helper method to delete files
    private void deleteFile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println(filePath + " deleted successfully.");
                } else {
                    System.out.println("Failed to delete " + filePath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // helper methods to remove the specific data written into the file after tests
    private void removeLineFromFile(String filePath, String lineToRemove) {
        try {
            File inputFile = new File(filePath);
            File tempFile = new File(filePath + ".tmp");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                // Skip lines that match the line to remove
                if (currentLine.trim().equals(lineToRemove)) continue;
                writer.write(currentLine + System.getProperty("line.separator"));
            }

            writer.close();
            reader.close();

            // Replace the original file with the updated one
            if (!inputFile.delete()) {
                System.err.println("Failed to delete original file: " + filePath);
            }
            if (!tempFile.renameTo(inputFile)) {
                System.err.println("Failed to rename temporary file: " + tempFile.getName());
            }

        } catch (IOException e) {
            System.err.println("Error processing file: " + filePath);
            e.printStackTrace();
        }
    }

    // Helper method to remove line that contains the specified word from the file
    private void removeLineContaining(String filePath, String textToFind) {
        File inputFile = new File(filePath);
        File tempFile = new File(filePath + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                // Skip lines that contain the text to find (e.g., CAD)
                if (currentLine.contains(textToFind)) {
                    continue;
                }
                writer.write(currentLine + System.lineSeparator());
            }

        } catch (IOException e) {
            System.err.println("Error processing file: " + filePath);
            e.printStackTrace();
        }

        // Replace the original file with the temporary file
        if (!inputFile.delete()) {
            System.err.println("Failed to delete original file: " + filePath);
        } else if (!tempFile.renameTo(inputFile)) {
            System.err.println("Failed to rename temporary file: " + tempFile.getName());
        }
    }

    // Test the currencies are loaded properly from files.
    @Test
    public void testLoadCurrencies() {
        // Test that currencies are loaded correctly
        assertFalse(currencyManager.currencies.isEmpty(), "Currencies should be loaded."); // should not be empty
        assertTrue(currencyManager.currencies.containsKey("USD"), "USD should be one of the loaded currencies.");
        assertTrue(currencyManager.currencies.containsKey("EUR"), "EUR should be one of the loaded currencies.");
    }

    // Test adding a currency rate for an existing currency and verifying that it is added correctly.
    @Test
    public void testAddCurrencyRate() {
        // Test adding a currency rate for an existing currency
        currencyManager.addCurrencyRate("USD", "EUR", 0.85, LocalDate.of(2024, 9, 12));

        Currency usd = currencyManager.getCurrency("USD");
        assertNotNull(usd, "USD should exist.");
        assertEquals(0.85, usd.getLatestRate("EUR"), 0.001, "EUR rate should be 0.85.");

        // Check if rate was written to file
        File usdFile = new File("src/TestDatabase/Currencies/USD.txt");
        assertTrue(usdFile.exists(), "USD file should exist in the test directory.");
    }

    // Test currency conversion functionality are correct between two currencies.
    @Test
    public void testConvertCurrency() {
        // Test currency conversion
        ArrayList<Object> result = currencyManager.convertCurrency(100, "USD", "EUR");
        assertEquals(2, result.size(), "Converted result should have two elements.");
        assertTrue((double) result.get(0) > 0, "Converted amount should be greater than 0.");
        assertEquals("EUR", result.get(1), "Currency symbol should be EUR.");
    }

    // Test adding a new currency and verifying that it is stored correctly.
    @Test
    public void testAddNewCurrency() throws Exception {
        // Test adding a new currency and check the effects
        currencyManager.addCurrency("CAD", "USD", 1.25, LocalDate.of(2024, 9, 12));

        Currency cad = currencyManager.getCurrency("CAD");
        assertNotNull(cad, "CAD should be added to the currency list.");
        assertEquals(1.25, cad.getLatestRate("USD"), 0.001, "CAD to USD rate should be 1.25.");

        // Check if the CAD file was created
        File cadFile = new File("src/TestDatabase/Currencies/CAD.txt");
        assertTrue(cadFile.exists(), "CAD file should be created in the test directory.");
    }

    // Test that an exception is thrown if the initial currency doesn't exist when adding a new currency.
    @Test
    public void testAddNewCurrencyThrowsException() {
        // Test that adding a currency without an existing initialCurrency throws an exception
        assertThrows(InstanceNotFoundException.class, () -> {
            currencyManager.addCurrency("CAD", "ABC", 1.25, LocalDate.of(2024, 9, 12));
        }, "Should throw InstanceNotFoundException if the initial currency is not found.");
    }

    // Test to verify the popular currencies list is loaded correctly.
    @Test
    public void testPopularCurrencies() {
        // Test if popular currencies are loaded correctly
        ArrayList<String> popularCurrencies = currencyManager.popularCurrencies;
        assertEquals(4, popularCurrencies.size(), "Popular currencies list should contain 4 items.");

        // Test setting popular currencies
        currencyManager.setPopularCurrencies();
        assertNotNull(popularCurrencies.get(0), "Popular currencies should be set correctly.");
    }

    @Test
    public void testCurrencyExist() {
        // Test that a currency exists in the manager
        assertTrue(currencyManager.currencyExist("USD"), "USD should exist.");
        assertFalse(currencyManager.currencyExist("XYZ"), "XYZ should not exist.");
    }

    @Test
    public void testProcessMenuAddRateInvalidInput() {
        // Simulate invalid input handling in processMenuAddRate (for example, when input is missing)
        // This test ensures the code can handle invalid inputs gracefully
        assertDoesNotThrow(() -> {
            currencyManager.processMenuAddRate();
        }, "Should handle invalid input without throwing an exception.");
    }

    @Test
    public void testProcessMenuAddRateValidInput() {
        // Simulate user input
        String input = "USD\nEUR\n1.25\n2024-09-15\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Execute the method
        currencyManager.processMenuAddRate();

        // Verify that the rate was added correctly
        Currency usd = currencyManager.getCurrency("USD");
        assertNotNull(usd.getRates().get("EUR"), "EUR should have been added as a rate for USD.");
    }

    @Test
    public void testProcessMenuAddRateInvalidCurrency() {
        // Simulate user input
        String input = "XYZ\nEUR\n1.25\n2024-09-15\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Execute the method
        currencyManager.processMenuAddRate();

        // Ensure that no rate is added for non-existent currency
        assertNull(currencyManager.getCurrency("XYZ"));
    }

//    @Test
//    public void testProcessMenuAddRateInvalidRateInput() {
//        // Simulate invalid exchange rate input
//        String input = "USD\nEUR\nabc\n2024-09-15\n";
//        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        // Expecting NumberFormatException
//        assertThrows(NumberFormatException.class, () -> currencyManager.processMenuAddRate());
//    }

    @Test
    public void testProcessMenuAddRateInvalidDateInput() {
        // Simulate invalid date input
        String input = "USD\nEUR\n1.25\ninvalid-date\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Expecting DateTimeParseException
        assertThrows(java.time.format.DateTimeParseException.class, () -> currencyManager.processMenuAddRate());
    }

    @Test
    public void testProcessMenuAddRateMissingInput() {
        // Simulate no input (blank lines)
        String input = "\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Ensure the method does not throw and exits cleanly
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate());
    }


    // Edge Case 1: Empty Input
    @Test
    public void testProcessMenuAddRateEmptyInput() {
        // Simulate pressing enter with no input
        String input = "\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should handle empty input gracefully without any issues
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate(), "Method should handle empty input gracefully.");
    }

    // Edge Case 2: Invalid Currency Name (empty string)
    @Test
    public void testProcessMenuAddRateInvalidCurrencyNameEmpty() {
        // Simulate an empty input for the fromCurrency, while providing valid inputs for the rest
        String input = "\nEUR\n1.25\n2024-09-15\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should handle empty currency name gracefully without errors
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate(), "Method should handle empty currency name gracefully.");
    }

    // Edge Case 2: Invalid Currency Name (special characters)
    @Test
    public void testProcessMenuAddRateInvalidCurrencyNameCharacters() {
        // Simulate an invalid currency name containing numbers
        String input = "USD1\nEUR\n1.25\n2024-09-15\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should handle invalid characters in the currency name gracefully
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate(), "Method should handle invalid characters in currency name.");
    }

    // Edge Case 3: Extreme Exchange Rate (zero)
    @Test
    public void testProcessMenuAddRateZeroExchangeRate() {
        // Simulate a zero exchange rate input
        String input = "USD\nEUR\n0\n2024-09-15\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should gracefully handle a zero exchange rate (depending on business rules)
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate(), "Method should handle zero exchange rate.");
    }

//    // Edge Case 3: Extreme Exchange Rate (negative)
//    @Test
//    public void testProcessMenuAddRateNegativeExchangeRate() {
//        // Simulate a negative exchange rate input
//        String input = "USD\nEUR\n-1\n2024-09-15\n";
//        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        // The method should throw a NumberFormatException for a negative exchange rate
//        assertThrows(NumberFormatException.class, () -> currencyManager.processMenuAddRate(), "Negative exchange rate should throw exception.");
//    }

    // Edge Case 4: Invalid Date (Feb 30)
    @Test
    public void testProcessMenuAddRateInvalidDateFeb30() {
        // Simulate an invalid date (e.g., February 30th doesn't exist)
        String input = "USD\nEUR\n1.25\n2024-02-30\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should throw DateTimeParseException for an invalid date
        assertThrows(java.time.format.DateTimeParseException.class, () -> currencyManager.processMenuAddRate(), "Invalid date should throw exception.");
    }

    // Edge Case 5: Same Currency (fromCurrency and toCurrency the same)
    @Test
    public void testProcessMenuAddRateSameCurrency() {
        // Simulate a case where both fromCurrency and toCurrency are the same
        String input = "USD\nUSD\n1.25\n2024-09-15\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should handle same currency input gracefully
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate(), "Same currency should be handled gracefully.");
    }

//    // Edge Case 6: Invalid Number Format in Exchange Rate
//    @Test
//    public void testProcessMenuAddRateInvalidNumberFormatExchangeRate() {
//        // Simulate an invalid number format for the exchange rate (e.g., with a comma)
//        String input = "USD\nEUR\n1,000\n2024-09-15\n";
//        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//
//        // The method should throw a NumberFormatException for an invalid number format
//        assertThrows(NumberFormatException.class, () -> currencyManager.processMenuAddRate(), "Invalid number format should throw exception.");
//    }

    // Edge Case 7: Whitespace around Inputs
    @Test
    public void testProcessMenuAddRateWhitespaceAroundInputs() {
        // Simulate inputs with leading and trailing whitespace
        String input = "  USD  \n  EUR  \n  1.25  \n  2024-09-15  \n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // The method should handle and trim the whitespace around inputs without issues
        assertDoesNotThrow(() -> currencyManager.processMenuAddRate(), "Method should trim whitespace and handle input correctly.");
    }
}
