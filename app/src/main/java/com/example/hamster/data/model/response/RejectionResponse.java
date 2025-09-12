package com.example.hamster.data.model.response;

import com.google.gson.annotations.SerializedName;

public class RejectionResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}