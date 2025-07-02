package scroll_tests;

import Edstemus.Scroll.FileConvertor;
import Edstemus.Scroll.Scroll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FileConvertorTest {

    private static final String TEST_OUTPUT_PATH = "src/test/test_resources/test_output.bin";
    private static final String TEST_FILE_PATH = "src/test/test_resources/test_scroll.txt";

    @BeforeEach
    public void setUp() {
        try (FileOutputStream fos = new FileOutputStream(TEST_FILE_PATH)) {
            fos.write("Test example".getBytes());
        } catch (IOException e) {
            fail("Failed to set up test file");
        }
    }

    @AfterEach
    public void tearDown() {
        File outputFile = new File(TEST_OUTPUT_PATH);
        if (outputFile.exists()) {
            outputFile.delete();
        }
        File testFile = new File(TEST_FILE_PATH);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    public void testConvertTxtToBin() {
        byte[] content = {65, 66, 67};
        FileConvertor.convertTxtToBin(content, TEST_OUTPUT_PATH);

        File file = new File(TEST_OUTPUT_PATH);
        assertTrue(file.exists(), "The binary file should be created");

        try {
            byte[] fileContent = Files.readAllBytes(Paths.get(TEST_OUTPUT_PATH));
            assertArrayEquals(content, fileContent, "File content should match input byte array");
        } catch (IOException e) {
            fail("IOException occurred while reading the output file");
        }
    }

    @Test
    public void testSaveContentToScroll() {
        int ownerID = 1;
        String scrollName = "TestScroll";
        int downloads = 0;

        Scroll scroll = FileConvertor.saveContentToScroll(TEST_FILE_PATH, ownerID, scrollName, downloads);
        assertNotNull(scroll, "Scroll should not be null");

        assertEquals(ownerID, scroll.getOwnerID(), "Owner ID should match");
        assertEquals(scrollName, scroll.getScrollName(), "Scroll name should match");
        assertEquals(downloads, scroll.getTotalDownloads(), "Downloads should match");
        assertEquals(LocalDate.now(), scroll.getUploadDate(), "Upload date should be today's date");
        assertArrayEquals("Test example".getBytes(), scroll.getScrollData(), "Scroll content should match file content");
    }

    @Test
    public void testSaveContentToScrollFileNotFound() {
        Scroll scroll = FileConvertor.saveContentToScroll("nonexistent.txt", 1, "InvalidScroll", 0);
        assertNull(scroll, "Scroll should be null when file is not found");
    }

    @Test
    public void testDisplayScrollContent() {
        byte[] content = "Test content".getBytes();
        Scroll scroll = new Scroll(1, "ScrollName", content, 0, LocalDate.now());
        scroll.setScrollID(1);
        String result = FileConvertor.displayScrollContent(scroll);
        assertNotNull(result, "Content should be displayed");
        assertEquals("Test content", result, "Displayed content should match expected content");
    }

    @Test
    public void testDisplayScrollContentEmpty() {
        byte[] content = "".getBytes();
        Scroll scroll = new Scroll(1, "EmptyScroll", content, 0, LocalDate.now());
        scroll.setScrollID(1);
        String result = FileConvertor.displayScrollContent(scroll);
        assertNull(result, "Empty content should return null");
    }

    @Test
    public void testDisplayScrollContentNull() {
        Scroll scroll = new Scroll(1, "NullScroll", null, 0, LocalDate.now());
        scroll.setScrollID(1);
        String result = FileConvertor.displayScrollContent(scroll);
        assertNull(result, "Null content should return null");
    }
}
