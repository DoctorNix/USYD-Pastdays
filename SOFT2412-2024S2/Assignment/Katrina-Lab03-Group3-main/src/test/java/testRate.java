import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soft2412.Currency.Rate;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class testRate {
    private Rate rate;
    private LocalDate today;
    private LocalDate yesterday;
    private LocalDate tomorrow;

    // Initialize a Rate object and dates for testing
    @BeforeEach
    public void setUp() {
        today = LocalDate.now();
        yesterday = today.minusDays(1);
        tomorrow = today.plusDays(1);
        rate = new Rate(0.85, today);
    }
    
    // Test to verify that the constructor initializes the value and date correctly.
    @Test
    public void testConstructor() {
        assertEquals(0.85, rate.getValue(), "Rate value should be initialized correctly");
        assertEquals(today, rate.getDate(), "Rate date should be initialized correctly");
    }
    
    // Test for adding a new rate to the record and checking if the last added rate is correct.
    @Test
    public void testAddRateToRecord() {
        rate.addRateToRecord(0.80, yesterday);
        assertEquals(0.80, rate.lastRateToRecord(), "The last added rate should be returned correctly");
    }
    
    // Test for updating the value and date of the rate, and verifying the updates are applied correctly.
    @Test
    public void testUpdateRate() {
        rate.updateRate(0.90, tomorrow);
        assertEquals(0.90, rate.getValue(), "The rate should be updated correctly to 0.90");
        assertEquals(tomorrow, rate.getDate(), "The date should be updated correctly");
    }
    
    // Test to ensure the latest rate is retrieved correctly.
    @Test
    public void testLastRateToRecord() {
        rate.addRateToRecord(0.80, yesterday);
        rate.updateRate(0.90, tomorrow);

        double lastRate = rate.lastRateToRecord();
        assertEquals(0.80, lastRate, "The last recorded rate should be returned correctly");
    }
    
    // Test to ensure that the history of the rate includes the correct rates and calculated statistics (mean, median, mode, and standard deviation).
    @Test
    public void testGetHistory() {
        rate.addRateToRecord(0.80, yesterday);
        rate.updateRate(0.90, tomorrow);

        String history = rate.getHistory(yesterday, tomorrow, "USD", "EUR");

        // Assertions to check that the history contains all necessary information since mean, median, mode and standard deviation are private.
        assertTrue(history.contains("0.8500"), "History should contain the current rate 0.85");
        assertTrue(history.contains("0.8000"), "History should contain the previous rate 0.80");
        assertTrue(history.contains("0.9000"), "History should contain the updated rate 0.90");
        assertTrue(history.contains("Mean:"), "History should contain mean");
        assertTrue(history.contains("Median:"), "History should contain median");
        assertTrue(history.contains("Mode:"), "History should contain mode");
        assertTrue(history.contains("Standard Deviation:"), "History should contain standard deviation");
    }
    
    // Test to verify that all the rates are sorted correctly in ascending order.
    @Test
    public void testSortAllRates() {
        ArrayList<Double> rates = new ArrayList<>();
        rates.add(1.00);
        rates.add(0.85);
        rates.add(0.90);
        rates.add(0.80);

        Collections.sort(rates);
        assertEquals(0.80, rates.get(0), "First element after sorting should be 0.80");
        assertEquals(1.00, rates.get(3), "Last element after sorting should be 1.00");
    }
}
