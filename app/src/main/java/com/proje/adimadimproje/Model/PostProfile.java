package com.proje.adimadimproje.Model;


            // Veritabanından tek tek çekme amacımız kullanıcının birden fazla resim seçebilme olanağıdır.
            //  String[] Image değişkenine String ImageSize sayısı kadar döngü içinde dönülerek seçilen tüm resimlerin alınması sağlanır


public class PostProfile {
    private String PostPID;
    private String UserID;
    private String PostPTitle;
    private String Time;
    private String PostPDate;
    private String PostPTime;
    private String ImageSize;
    private String[] Image;

    public PostProfile(String postPID, String userID, String postPTitle, String time, String postPDate, String postPTime, String imageSize, String[] image) {
        PostPID = postPID;
        UserID = userID;
        PostPTitle = postPTitle;
        Time = time;
        PostPDate = postPDate;
        PostPTime = postPTime;
        ImageSize = imageSize;
        Image = image;
    }

    public String getPostPID() {
        return PostPID;
    }

    public void setPostPID(String postPID) {
        PostPID = postPID;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        this.UserID = userID;
    }

    public String getPostPTitle() {
        return PostPTitle;
    }

    public void setPostPTitle(String postPTitle) {
        PostPTitle = postPTitle;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getPostPDate() {
        return PostPDate;
    }

    public void setPostPDate(String postPDate) {
        PostPDate = postPDate;
    }

    public String getPostPTime() {
        return PostPTime;
    }

    public void setPostPTime(String postPTime) {
        PostPTime = postPTime;
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
