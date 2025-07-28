package com.example.hamster.data.model.request;

public class CreateNotificationRequest {
    private String title;
    private String message;
    private String userId;
    private String type;

    public CreateNotificationRequest(String title, String message, String userId, String type) {
        this.title = title;
        this.message = message;
        this.userId = userId;
        this.type = type;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}

