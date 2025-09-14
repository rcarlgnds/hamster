package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Notification {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String message;

    @SerializedName("type")
    private String type;

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("createdAt")
    private Date createdAt;

    @SerializedName("copyString")
    private String copyString;

    @SerializedName("link")
    private String link;

    @SerializedName("data")
    private NotificationData data;


    // --- Getters ---
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public Date getCreatedAt() { return createdAt; }
    public String getCopyString() { return copyString; }
    public String getLink() { return link; }
    public NotificationData getData() { return data; }
    public void setRead(boolean read) {
        isRead = read;
    }
}