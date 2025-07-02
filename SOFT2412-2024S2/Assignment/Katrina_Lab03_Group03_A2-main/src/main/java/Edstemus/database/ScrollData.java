package Edstemus.database;

import Edstemus.GUI.SearchField;
import Edstemus.Scroll.Scroll;
import Edstemus.Scroll.ScrollSearchOptions;
import Edstemus.database.security.PasswordHasher;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScrollData {

    private DatabaseManager dbManager;
    private HashMap<String, Scroll> nameToScroll = new HashMap<>();


    public ScrollData(){
        dbManager = DatabaseManager.getInstance();
        loadScrollsFromDatabase();
    }

    private Scroll createScrollFromSql(ResultSet rs){
        try{
            String name = rs.getString("name");
            int id = rs.getInt("ID");
            byte[] content = rs.getBytes("content");
            int uploader_id = rs.getInt("uploader_id");
            int downloads = rs.getInt("downloads");
            LocalDate upload_date = rs.getDate("upload_date").toLocalDate();

            Scroll scroll = new Scroll(uploader_id, name, content, downloads, upload_date);
            scroll.setScrollID(id);

            String password = rs.getString("password");
            if(password != null){
                System.out.println("Scroll with password: " + name);
                scroll.setPassword(password);
            }else{
                scroll.setPassword("");
            }

            return scroll;

        }
        catch(SQLException e){
            return null;
        }
    }

    // when uploading a scroll, user can save the new scroll into database
    public boolean insertScroll(Scroll scroll){
        String query = "INSERT INTO Scroll (name, content, uploader_id, downloads, upload_date, password) VALUES (?, ?, ?, ?, ?, ?)";

        String[] generatedColumns = { "ID" };

        try(Connection conn = dbManager.connect();
            PreparedStatement stmt = conn.prepareStatement(query, generatedColumns)){

            stmt.setString(1, scroll.getScrollName());
            stmt.setBytes(2, scroll.getScrollData());
            stmt.setInt(3, scroll.getOwnerID());
            stmt.setInt(4, scroll.getTotalDownloads());
            stmt.setDate(5, java.sql.Date.valueOf(scroll.getUploadDate()));

            if (scroll.getPassword() != null && !scroll.getPassword().isEmpty()) {
                stmt.setString(6, PasswordHasher.hashPassword(scroll.getPassword()));
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            int rowsInserted = stmt.executeUpdate();

            if (rowsInserted > 0) {

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int generatedID = generatedKeys.getInt(1);
                    scroll.setScrollID(generatedID);
                    System.out.println("Scroll inserted with ID: " + generatedID);
                    return true;
                }
            }

        } catch(SQLException e){
            System.out.println(e);
        }

        return false;
    }

    public boolean deleteScroll(int scrollID, int uploaderID){
        String query = "DELETE FROM Scroll WHERE ID = ? AND UPLOADER_ID = ?";
        try(Connection conn = dbManager.connect();
            PreparedStatement stmt = conn.prepareStatement(query)){

            stmt.setInt(1, scrollID);
            stmt.setInt(2, uploaderID);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0){
                nameToScroll.values().removeIf(scroll -> scroll.getScrollID() == scrollID);
                System.out.println("Scroll deleted success: ID" + scrollID);
                return true;
            } else {
                System.out.println("Scroll ID" + scrollID + " not found or unauthorized");
                return false;
            }

        } catch(SQLException e){
            System.out.println("Error in delete: "+e.getMessage());
            return false;
        }
    }

    public ArrayList<String> getAllScrollNames(){
        loadScrollsFromDatabase();
        ArrayList<String> scrollNames = new ArrayList<>();
        scrollNames.addAll(nameToScroll.keySet());
        return scrollNames;
    }

    public void loadScrollsFromDatabase(){    //run on startup to get all scrolls

        String sql = "SELECT * FROM Scroll";

        try{
            Connection conn = dbManager.connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Scroll current_scroll = createScrollFromSql(rs);
                if (current_scroll == null) {
                    throw new SQLException("Error in retrieving scroll");
                }
                else{
                    nameToScroll.put(current_scroll.getScrollName(), current_scroll);
                }
            }
        }
        catch(SQLException e){
            System.out.println(e);
        }
    }

    public ArrayList<String> displayAllScrolls() {
        loadScrollsFromDatabase();

        ArrayList<String> allScrolls = new ArrayList<>();
        if (nameToScroll != null) {
            allScrolls.addAll(nameToScroll.keySet());
        }
        return allScrolls;
    }

    public Scroll getScroll(String name){
        loadScrollsFromDatabase();
        return nameToScroll.get(name);
    }

    public Scroll updateScrollData(byte[] new_content, Scroll current) {

        String query = "UPDATE Scroll SET content = (?) WHERE ID = (?)";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setBytes(1, new_content);
            stmt.setInt(2, current.getScrollID());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                current.setScrollData(new_content);

                nameToScroll.put(current.getScrollName(), current);

                return current;
            }

        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public List<Scroll> searchScrolls(ScrollSearchOptions criteria) {
        List<Scroll> scrolls = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Scroll WHERE 1=1");

        if (criteria.hasCriteria(SearchField.SCROLL_NAME)) {
            query.append(" AND name LIKE ?");
        }
        if (criteria.hasCriteria(SearchField.SCROLL_ID)) {
            query.append(" AND ID = ?");
        }
        if (criteria.hasCriteria(SearchField.OWNER_ID)) {
            query.append(" AND uploader_id = ?");
        }
        if (criteria.hasCriteria(SearchField.UPLOAD_DATE_FROM)) {
            query.append(" AND upload_date >= ?");
        }
        if (criteria.hasCriteria(SearchField.UPLOAD_DATE_TO)) {
            query.append(" AND upload_date <= ?");
        }
        if (criteria.hasCriteria(SearchField.MIN_DOWNLOADS)) {
            query.append(" AND downloads >= ?");
        }
        if (criteria.hasCriteria(SearchField.MAX_DOWNLOADS)) {
            query.append(" AND downloads <= ?");
        }

        try (Connection conn = dbManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            int index = 1;

            // Set parameters dynamically based on criteria map
            if (criteria.hasCriteria(SearchField.SCROLL_NAME)) {
                pstmt.setString(index++, "%" + criteria.getCriteria(SearchField.SCROLL_NAME) + "%");
            }
            if (criteria.hasCriteria(SearchField.SCROLL_ID)) {
                pstmt.setInt(index++, (Integer) criteria.getCriteria(SearchField.SCROLL_ID));
            }
            if (criteria.hasCriteria(SearchField.OWNER_ID)) {
                pstmt.setInt(index++, (Integer) criteria.getCriteria(SearchField.OWNER_ID));
            }
            if (criteria.hasCriteria(SearchField.UPLOAD_DATE_FROM)) {
                pstmt.setDate(index++, Date.valueOf((LocalDate) criteria.getCriteria(SearchField.UPLOAD_DATE_FROM)));
            }
            if (criteria.hasCriteria(SearchField.UPLOAD_DATE_TO)) {
                pstmt.setDate(index++, Date.valueOf((LocalDate) criteria.getCriteria(SearchField.UPLOAD_DATE_TO)));
            }
            if (criteria.hasCriteria(SearchField.MIN_DOWNLOADS)) {
                pstmt.setInt(index++, (Integer) criteria.getCriteria(SearchField.MIN_DOWNLOADS));
            }
            if (criteria.hasCriteria(SearchField.MAX_DOWNLOADS)) {
                pstmt.setInt(index++, (Integer) criteria.getCriteria(SearchField.MAX_DOWNLOADS));
            }

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Scroll scroll = new Scroll(
                        rs.getInt("uploader_id"),
                        rs.getString("name"),
                        rs.getBytes("content"),
                        rs.getInt("downloads"),
                        rs.getDate("upload_date").toLocalDate()
                );
                scroll.setScrollID(rs.getInt("id"));
                scrolls.add(scroll);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return scrolls;
    }

    public void increaseDownload(int scrollID){
        String query = "UPDATE Scroll SET downloads = downloads + 1 WHERE ID = (?)";

        try(Connection conn = dbManager.connect();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1,scrollID);
            stmt.executeUpdate();
            System.out.println("Scroll Download Updated");
            loadScrollsFromDatabase();
        } catch(SQLException e){
            System.out.println(e);
        }
    }



    public Scroll getScrollOfTheDay() {
        LocalDate today = LocalDate.now();
        System.out.println(today);

        Scroll todayScroll = getTodayScrollFromDatabase(today);
        if (todayScroll != null) {
            return todayScroll;
        }

        List<Integer> allScrollIDs = new ArrayList<>();
        String idSQL = "SELECT ID FROM Scroll WHERE password IS NULL OR password = ''";
        String chosenSQL = "SELECT scroll_id FROM ScrollOfTheDay";

        try (Connection conn = dbManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(idSQL)) {

            while (rs.next()) {
                allScrollIDs.add(rs.getInt("ID"));
            }

            List<Integer> assignedScrollIds = new ArrayList<>();
            ResultSet chosenRs = stmt.executeQuery(chosenSQL);

            while (chosenRs.next()) {
                assignedScrollIds.add(chosenRs.getInt("scroll_id"));
            }

            if (assignedScrollIds.containsAll(allScrollIDs)) {
                resetScrollOfTheDayTable();
                assignedScrollIds.clear();
            }


            allScrollIDs.removeAll(assignedScrollIds);


        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return null;
        }
        // if all the scrolls have benen picked, reset the list
        if (allScrollIDs.isEmpty()) {
            System.out.println("No scrolls in db");
            return null;

        }

        Random random = new Random();
        int randomIndex = random.nextInt(allScrollIDs.size());
        int chosenScrollID = allScrollIDs.get(randomIndex);

        Scroll selectedScroll = getScrollById(chosenScrollID);

        if (selectedScroll != null){
            saveTodayScrollToDatabase(today, chosenScrollID);
        }

        return selectedScroll;
    }

    public Scroll getTodayScrollFromDatabase(LocalDate date) {
        String query = "SELECT * FROM ScrollOfTheDay INNER JOIN Scroll ON Scroll.ID = ScrollOfTheDay.scroll_id WHERE selected_date = ?";

        try (Connection conn = dbManager.connect();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createScrollFromSql(rs);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return null;
    }

    public void saveTodayScrollToDatabase(LocalDate date, int scrollId) {
        String insertOrUpdateSQL = "INSERT OR REPLACE INTO ScrollOfTheDay (selected_date, scroll_id) VALUES (?, ?)";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(insertOrUpdateSQL)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setInt(2, scrollId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }

    private Scroll getScrollById(int id) {
        String sql = "SELECT * FROM Scroll WHERE ID = (?)";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return createScrollFromSql(rs);
            }

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }

        return null;
    }

    public void resetScrollOfTheDayTable() {
        String deleteSQL = "DELETE FROM ScrollOfTheDay";

        try (Connection conn = dbManager.connect();
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {
            stmt.executeUpdate();
            System.out.println("ScrollOfTheDay table reset. Starting new cycle.");
        } catch (SQLException e) {
            System.out.println("SQL Error during reset: " + e.getMessage());
        }
    }

    public List<Scroll> getScrollsByDownloadCount(){
        List<Scroll> allScrolls = new ArrayList<>(nameToScroll.values());
        allScrolls.sort((s1, s2) -> Integer.compare(s2.getTotalDownloads(), s1.getTotalDownloads()));
        return allScrolls;
    }

}

