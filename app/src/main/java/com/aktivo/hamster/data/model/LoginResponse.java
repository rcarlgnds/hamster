package com.aktivo.hamster.data.model;

public class LoginResponse {
    private boolean success;
    private String message;
    private LoginData data;
    private long timestamp;

    // Getters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public LoginData getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}