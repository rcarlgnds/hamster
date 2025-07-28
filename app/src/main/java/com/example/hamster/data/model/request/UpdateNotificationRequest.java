package com.example.hamster.data.model.request;

public class UpdateNotificationRequest {
    private boolean read;

    public UpdateNotificationRequest(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }
    public void setRead(boolean read) {
        this.read = read;
    }
}

