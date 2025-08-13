package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Notification;

import java.util.List;

public class NotificationResponse {
    private boolean success;
    private String message;
    private List<Notification> data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Notification> getData() {
        return data;
    }
}