package com.proje.adimadimproje.Model;

public class User {
    private String ID;
    private String UserNameLastName;
    private String UserName;
    private String UserEmail;
    private String UserImage;
    private String UserBackgroundImage;

    public User() {

    }
    public User(String ID, String userNameLastName, String userName, String userEmail, String userImage, String userBackgroundImage) {
        this.ID = ID;
        this.UserNameLastName = userNameLastName;
        this.UserName = userName;
        this.UserEmail = userEmail;
        this.UserImage = userImage;
        this.UserBackgroundImage = userBackgroundImage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUserNameLastName() {
        return UserNameLastName;
    }

    public void setUserNameLastName(String userNameLastName) {
        UserNameLastName = userNameLastName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }

    public String getUserImage() {
        return UserImage;
    }

    public void setUserImage(String userImage) {
        UserImage = userImage;
    }

    public String getUserBackgroundImage() {
        return UserBackgroundImage;
    }

    public void setUserBackgroundImage(String userBackgroundImage) {
        UserBackgroundImage = userBackgroundImage;
    }
}