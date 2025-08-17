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

    @SerializedName("isRead")
    private boolean isRead;

    @SerializedName("createdAt")
    private long createdAt;

    @SerializedName("data")
    private NotificationData data;

    // --- Getters ---
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return isRead;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public NotificationData getData() {
        return data;
    }

    // --- Setter ---
    public void setRead(boolean read) { // Setter yang hilang
        isRead = read;
    }
}