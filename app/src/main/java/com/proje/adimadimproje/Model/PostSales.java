package com.proje.adimadimproje.Model;


            // Veritabanından tek tek çekme amacımız kullanıcının birden fazla resim seçebilme olanağıdır.
            //  String[] Image değişkenine String ImageSize sayısı kadar döngü içinde dönülerek seçilen tüm resimlerin alınması sağlanır

public class PostSales {
    private String PostSID;
    private String UserID;
    private String PostSStatus;
    private String PostSCategory;
    private String PostSTitle;
    private String PostSComment;
    private String PostSPrice;
    private String PostSTag1;
    private String PostSTag2;
    private String PostSTag3;
    private String PostSCCName1;
    private String PostSCCName2;
    private String PostSCCName3;
    private String Time;
    private String PostSDate;
    private String PostSTime;
    private String ImageSize;
    private String[] Image;

    public PostSales(String postSID, String userID, String postSStatus, String postSCategory, String postSTitle, String postSComment, String postSPrice, String postSTag1, String postSTag2, String postSTag3, String postSCCName1, String postSCCName2, String postSCCName3, String time, String postSDate, String postSTime, String imageSize, String[] image) {
        PostSID = postSID;
        UserID = userID;
        PostSStatus = postSStatus;
        PostSCategory = postSCategory;
        PostSTitle = postSTitle;
        PostSComment = postSComment;
        PostSPrice = postSPrice;
        PostSTag1 = postSTag1;
        PostSTag2 = postSTag2;
        PostSTag3 = postSTag3;
        PostSCCName1 = postSCCName1;
        PostSCCName2 = postSCCName2;
        PostSCCName3 = postSCCName3;
        Time = time;
        PostSDate = postSDate;
        PostSTime = postSTime;
        ImageSize = imageSize;
        Image = image;
    }

    public String getPostSID() {
        return PostSID;
    }

    public void setPostSID(String postSID) {
        PostSID = postSID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getPostSStatus() {
        return PostSStatus;
    }

    public void setPostSStatus(String postSStatus) {
        PostSStatus = postSStatus;
    }

    public String getPostSCategory() {
        return PostSCategory;
    }

    public void setPostSCategory(String postSCategory) {
        PostSCategory = postSCategory;
    }

    public String getPostSTitle() {
        return PostSTitle;
    }

    public void setPostSTitle(String postSTitle) {
        PostSTitle = postSTitle;
    }

    public String getPostSComment() {
        return PostSComment;
    }

    public void setPostSComment(String postSComment) {
        PostSComment = postSComment;
    }

    public String getPostSPrice() {
        return PostSPrice;
    }

    public void setPostSPrice(String postSPrice) {
        PostSPrice = postSPrice;
    }

    public String getPostSTag1() {
        return PostSTag1;
    }

    public void setPostSTag1(String postSTag1) {
        PostSTag1 = postSTag1;
    }

    public String getPostSTag2() {
        return PostSTag2;
    }

    public void setPostSTag2(String postSTag2) {
        PostSTag2 = postSTag2;
    }

    public String getPostSTag3() {
        return PostSTag3;
    }

    public void setPostSTag3(String postSTag3) {
        PostSTag3 = postSTag3;
    }

    public String getPostSCCName1() {
        return PostSCCName1;
    }

    public void setPostSCCName1(String postSCCName1) {
        PostSCCName1 = postSCCName1;
    }

    public String getPostSCCName2() {
        return PostSCCName2;
    }

    public void setPostSCCName2(String postSCCName2) {
        PostSCCName2 = postSCCName2;
    }

    public String getPostSCCName3() {
        return PostSCCName3;
    }

    public void setPostSCCName3(String postSCCName3) {
        PostSCCName3 = postSCCName3;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPostSDate() {
        return PostSDate;
    }

    public void setPostSDate(String postSDate) {
        PostSDate = postSDate;
    }

    public String getPostSTime() {
        return PostSTime;
    }

    public void setPostSTime(String postSTime) {
        PostSTime = postSTime;
    }

    public String getImageSize() {
        return ImageSize;
    }

    public void setImageSize(String imageSize) {
        ImageSize = imageSize;
    }

    public String[] getImage() {
        return Image;
    }

    public void setImage(String[] image) {
        Image = image;
    }
}
