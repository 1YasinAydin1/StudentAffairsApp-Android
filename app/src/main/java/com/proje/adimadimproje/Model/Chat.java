package com.proje.adimadimproje.Model;

public class Chat {
    private String SenderUserID;
    private String ReceiverUserID;
    private String Message;
    private String Date;
    private String Time;

    public Chat(){

    }
    public Chat(String senderUserID, String receiverUserID, String message, String date, String time) {
        this.SenderUserID = senderUserID;
        this.ReceiverUserID = receiverUserID;
        this.Message = message;
        this.Date = date;
        this.Time = time;
    }

    public String getSenderUserID() {
        return SenderUserID;
    }

    public void setSenderUserID(String senderUserID) {
        SenderUserID = senderUserID;
    }

    public String getReceiverUserID() {
        return ReceiverUserID;
    }

    public void setReceiverUserID(String receiverUserID) {
        ReceiverUserID = receiverUserID;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
