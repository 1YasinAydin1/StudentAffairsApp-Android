package com.proje.adimadimproje.Model;

public class Comment {

    private String Comment;
    private String userID;
    private String PostControl;

    public Comment() {
    }

    public Comment(String comment, String userID, String postControl) {
        Comment = comment;
        this.userID = userID;
        PostControl = postControl;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostControl() {
        return PostControl;
    }

    public void setPostControl(String postControl) {
        PostControl = postControl;
    }
}
