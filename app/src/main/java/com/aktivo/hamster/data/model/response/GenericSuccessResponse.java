package com.aktivo.hamster.data.model.response;

import com.google.gson.annotations.SerializedName;

public class GenericSuccessResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}