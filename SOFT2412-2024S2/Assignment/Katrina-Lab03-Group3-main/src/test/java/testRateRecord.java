import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import soft2412.Currency.RateRecord;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class testRateRecord {
    private RateRecord rateRecord;
    
    // Initializes a RateRecord object with a value of 1.23 and a specific date to run before each test.
    @BeforeEach
    public void setUp() {
        rateRecord = new RateRecord(1.23, LocalDate.of(2024, 9, 1));
    }
    
    // Test to ensure the getValue method of the RateRecord returns the correct value.
    @Test
    public void testGetValue() {
        assertEquals(1.23, rateRecord.getValue(), 0.001,"Rate should be 1.23");
    }
    
    // Test to ensure the getDate method of the RateRecord returns the correct date.
    @Test
    public void testGetDate() {
        assertEquals(LocalDate.of(2024, 9, 1), rateRecord.getDate(), "Date should be 2024-09-01");
    }
}
