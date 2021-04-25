package com.proje.adimadimproje.Model;

public class Like {
    private String UserID;
    private String Text;
    private String PostID;
    private String PostName;
    private boolean IsPost;

    public Like(){}

    public Like(String userID, String text, String postID, String postName, boolean isPost) {
        UserID = userID;
        Text = text;
        PostID = postID;
        PostName = postName;
        IsPost = isPost;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getPostID() {
        return PostID;
    }

    public void setPostID(String postID) {
        PostID = postID;
    }

    public String getPostName() {
        return PostName;
    }

    public void setPostName(String postName) {
        PostName = postName;
    }

    public boolean getIsPost() {
        return IsPost;
    }

    public void setIsPost(boolean post) {
        IsPost = post;
    }
}
