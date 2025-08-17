package com.example.hamster.data.model.request;

public class UpdateNotificationRequest {
    private final boolean isRead;

    public UpdateNotificationRequest(boolean isRead) {
        this.isRead = isRead;
    }
}