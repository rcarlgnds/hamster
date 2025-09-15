package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.NotificationData;
import com.google.gson.annotations.SerializedName;

public class NotificationResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private NotificationData data;

    public boolean isSuccess() {
        return success;
    }

    public NotificationData getData() {
        return data;
    }
}