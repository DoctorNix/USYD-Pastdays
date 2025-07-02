package Edstemus.Scroll;

import java.time.LocalDate;


public class Scroll {
    private int ownerID;
    private int scrollID;
    private String scrollName;
    private int totalDownloads;
    private byte[] scrollData;
    private LocalDate uploadDate;
    private String password;

    public Scroll(int ownerID, String scrollName, byte[] scrollData, int totalDownloads, LocalDate uploadDate) {
        this.ownerID = ownerID;
        this.scrollName = scrollName;
        this.scrollData = scrollData;
        this.scrollID = scrollID;
        this.password = "";
        this.uploadDate = uploadDate;
        this.totalDownloads = totalDownloads;
    }

    public boolean doesScrollNeedPassword() {
        return password != null && !password.isEmpty();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public int getScrollID() {
        return scrollID;
    }

    public String getPassword(){
        return password;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public int getTotalDownloads() {
        return totalDownloads;
    }

    public byte[] getScrollData() {
        return scrollData;
    }

    public String getScrollName() {
        return scrollName;
    }

    public void setScrollID(int scrollID){
        this.scrollID = scrollID;
    }

    public void changeScrollName(String newScrollName) {
        this.scrollName = newScrollName;
    }

    public void setScrollData(byte[] bytes) {
        this.scrollData = bytes;
    }
}
