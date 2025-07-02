package scroll_tests;

import Edstemus.Scroll.Scroll;
import Edstemus.database.DatabaseManager;
import Edstemus.database.ScrollData;
import Edstemus.database.UserData;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScrollTest {
    private DatabaseManager dbManager;
    public ScrollData scrollData = new ScrollData();


    @BeforeAll
    public static void setUpOnce() {
        DatabaseManager.getInstance().setDatabasePath("jdbc:sqlite:src/test/test_resources/test_db/edstemus_test.db");
    }

    @BeforeEach
    public void setUp(){
        dbManager = DatabaseManager.getInstance();
        dbManager.createUsersTable();
        dbManager.createScrollTable();
    }

    @AfterEach
    public void cleanUpTables() {
        try (Connection conn = dbManager.connect();
             Statement st = conn.createStatement()) {
            st.executeUpdate("DELETE FROM Users");
            st.executeUpdate("DELETE FROM Scroll");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void tearDown(){
        File dbFile = new File("src/test/test_resources/test_db/edstemus_test.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    public void testScrollData(){
        Scroll testScroll = new Scroll(1, "testScroll", "content".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(testScroll);
        Scroll insertedScroll = scrollData.getScroll("testScroll");
        int ownerID = insertedScroll.getOwnerID();
        String scrollName = insertedScroll.getScrollName();
        byte[] scrollContent = insertedScroll.getScrollData();
        int currentDownloads = insertedScroll.getTotalDownloads();
        LocalDate uploadDate = insertedScroll.getUploadDate();

        assertEquals(ownerID, 1, "expected ownerID different from object");
        assertEquals(scrollName, "testScroll", "expected name different from object");
        assertArrayEquals(scrollContent, "content".getBytes(), "expected content different from object");
        assertEquals(currentDownloads, 0, "expected currentDownloads different from object");
        assertEquals(uploadDate, LocalDate.now(), "expected uploadDate different from object");
    }

    @Test
    public void testContentUpdate(){
        Scroll testScroll = new Scroll(1, "testScroll", "content".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(testScroll);
        Scroll insertedScroll = scrollData.getScroll("testScroll");
        scrollData.updateScrollData("newContent".getBytes(), insertedScroll);
        Scroll updatedScroll = scrollData.getScroll("testScroll");
        assertArrayEquals(updatedScroll.getScrollData(), "newContent".getBytes(), "Error in changing content");
    }

    @Test
    public void testRetrieveAllScrollNames(){
        Scroll scroll1 = new Scroll(1, "testScroll1", "content1".getBytes(), 0, LocalDate.now());
        Scroll scroll2 = new Scroll(2, "testScroll2", "content2".getBytes(), 0, LocalDate.now());
        Scroll scroll3 = new Scroll(2, "testScroll3", "content3".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(scroll1);
        scrollData.insertScroll(scroll2);
        scrollData.insertScroll(scroll3);

        ArrayList<String> allScrolls = new ArrayList<>();
        allScrolls.add("testScroll1");
        allScrolls.add("testScroll2");
        allScrolls.add("testScroll3");

        ArrayList<String> returnedScrolls = scrollData.getAllScrollNames();

        assertEquals(allScrolls, returnedScrolls, "Returned list of scrolls different from expected");
    }

    @Test
    public void testDownloadCounter(){
        Scroll testScroll = new Scroll(123, "testScroll", "randomcontent".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(testScroll);
        int currentID = scrollData.getScroll("testScroll").getScrollID();
        scrollData.increaseDownload(currentID);
        int newDownloads = scrollData.getScroll("testScroll").getTotalDownloads();
        assertEquals(1, newDownloads, "Scroll object should have 1 download, has " + newDownloads);
    }

    @Test
    public void testMultipleDownloadIncrements() {
        Scroll testScroll = new Scroll(1, "testScroll", "content".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(testScroll);

        int scrollID = scrollData.getScroll("testScroll").getScrollID();

        // Increment the download counter multiple times
        scrollData.increaseDownload(scrollID);
        scrollData.increaseDownload(scrollID);
        scrollData.increaseDownload(scrollID);

        // Fetch the updated scroll and verify the download count
        Scroll updatedScroll = scrollData.getScroll("testScroll");
        assertEquals(3, updatedScroll.getTotalDownloads(), "Download count should be 3.");
    }

    @Test
    public void testGetScrollsByDownloadCount() {
        // Insert scrolls with varying download counts
        Scroll scroll1 = new Scroll(1, "Scroll 1", "content1".getBytes(), 5, LocalDate.now());
        Scroll scroll2 = new Scroll(2, "Scroll 2", "content2".getBytes(), 10, LocalDate.now());
        Scroll scroll3 = new Scroll(3, "Scroll 3", "content3".getBytes(), 2, LocalDate.now());

        scrollData.insertScroll(scroll1);
        scrollData.insertScroll(scroll2);
        scrollData.insertScroll(scroll3);

        // Fetch scrolls sorted by download count
        List<Scroll> sortedScrolls = scrollData.getScrollsByDownloadCount();

        // Verify the order of the scrolls
        //assertEquals("Scroll 2", sortedScrolls.get(0).getScrollName(), "Expected most downloaded scroll first.");
        //assertEquals("Scroll 1", sortedScrolls.get(1).getScrollName(), "Expected second most downloaded scroll.");
        //assertEquals("Scroll 3", sortedScrolls.get(2).getScrollName(), "Expected least downloaded scroll.");
    }

    @Test
    public void testScrollWithZeroDownloads() {
        Scroll scroll1 = new Scroll(1, "Scroll 1", "content1".getBytes(), 5, LocalDate.now());
        Scroll scroll2 = new Scroll(2, "Scroll 2", "content2".getBytes(), 0, LocalDate.now()); // Zero downloads

        scrollData.insertScroll(scroll1);
        scrollData.insertScroll(scroll2);

        // Fetch scrolls sorted by download count
        List<Scroll> sortedScrolls = scrollData.getScrollsByDownloadCount();

        // Verify that the scroll with zero downloads is last
        //assertEquals("Scroll 1", sortedScrolls.get(0).getScrollName(), "Expected most downloaded scroll first.");
        //assertEquals("Scroll 2", sortedScrolls.get(1).getScrollName(), "Expected scroll with zero downloads last.");
    }

    @Test
    public void testIncrementAndRetrieveStatistics() {
        Scroll scroll = new Scroll(1, "StatScroll", "content".getBytes(), 0, LocalDate.now());
        scrollData.insertScroll(scroll);

        int scrollID = scrollData.getScroll("StatScroll").getScrollID();

        // Increment downloads
        scrollData.increaseDownload(scrollID);
        scrollData.increaseDownload(scrollID);

        // Fetch scrolls sorted by download count
        List<Scroll> sortedScrolls = scrollData.getScrollsByDownloadCount();

        // Verify the statistics
        assertEquals(2, sortedScrolls.get(0).getTotalDownloads(), "Expected 2 downloads.");
        assertEquals("StatScroll", sortedScrolls.get(0).getScrollName(), "Expected 'StatScroll' to be first.");
    }
    @Test
    public void testDoesScrollNeedPasswordWithPassword(){
        Scroll scrollWithPassword = new Scroll(1, "ProtectedScroll", "protected content".getBytes(), 0, LocalDate.now());
        scrollWithPassword.setPassword("hashedPassword");

        assertTrue(scrollWithPassword.doesScrollNeedPassword(), "Scroll should require a password");
    }

    @Test
    public void testDoesScrollNeedPasswordNoPassword(){
        Scroll scrollWithoutPassword = new Scroll(1, "UnprotectedScroll", "unprotected content".getBytes(), 0, LocalDate.now());

        assertFalse(scrollWithoutPassword.doesScrollNeedPassword(), "Scroll should not require a password");

    }
}
