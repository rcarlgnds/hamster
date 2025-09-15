package com.aktivo.hamster.data.model;

public class ApiError {
    private String message;
    private int statusCode;
    private String error;
    private String timestamp;
    private String path;

    public String getMessage() {
        return message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }
}

