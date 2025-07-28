package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;

public class Notification implements Serializable {
    private String id;
    private String title;
    private String body;
    private String link;
    private NotificationType type;
    private String userId;
    private boolean isRead;
    private Date readAt;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private User user;

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public NotificationType getType() { return type; }
    public boolean isRead() { return isRead; }
}