package scroll_tests;

import Edstemus.GUI.SearchField;
import Edstemus.Scroll.ScrollSearchOptions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ScrollSearchOptionsTest {

        @Test
        public void testAddAndGetCriteria() {
            ScrollSearchOptions options = new ScrollSearchOptions();
            SearchField field = SearchField.SCROLL_NAME;
            String value = "Test Scroll";

            options.addCriteria(field, value);
            assertEquals(value, options.getCriteria(field), "The criteria value should be 'Test Scroll'");
        }

        @Test
        public void testHasCriteria() {
            ScrollSearchOptions options = new ScrollSearchOptions();
            SearchField field = SearchField.OWNER_ID;
            int value = 10;

            options.addCriteria(field, value);

            assertTrue(options.hasCriteria(field), "The criteria should contain the field OWNER_ID");
        }

        @Test
        public void testGetAllCriteria() {
            ScrollSearchOptions options = new ScrollSearchOptions();
            SearchField field1 = SearchField.UPLOAD_DATE_FROM;
            SearchField field2 = SearchField.UPLOAD_DATE_TO;
            String value1 = "2024-01-01";
            String value2 = "2024-12-31";

            options.addCriteria(field1, value1);
            options.addCriteria(field2, value2);

            Map<SearchField, Object> allCriteria = options.getAllCriteria();
            assertEquals(2, allCriteria.size(), "There should be 2 criteria in the map");
            assertEquals(value1, allCriteria.get(field1), "UPLOAD_DATE_FROM should be '2024-01-01'");
            assertEquals(value2, allCriteria.get(field2), "UPLOAD_DATE_TO should be '2024-12-31'");
        }

        @Test
        public void testOverwriteCriteria() {
            ScrollSearchOptions options = new ScrollSearchOptions();
            SearchField field = SearchField.MIN_DOWNLOADS;
            int initialValue = 1;
            int updatedValue = 3;

            options.addCriteria(field, initialValue);
            assertEquals(initialValue, options.getCriteria(field), "The initial value should be 1");

            options.addCriteria(field, updatedValue);
            assertEquals(updatedValue, options.getCriteria(field), "The value should be updated to 3");
        }

        @Test
        public void testEmptyCriteria() {
            ScrollSearchOptions options = new ScrollSearchOptions();

            assertFalse(options.hasCriteria(SearchField.MAX_DOWNLOADS), "Initially, there should be no criteria for MAX_DOWNLOADS");
            assertTrue(options.getAllCriteria().isEmpty(), "The criteria map should initially be empty");
        }
}



