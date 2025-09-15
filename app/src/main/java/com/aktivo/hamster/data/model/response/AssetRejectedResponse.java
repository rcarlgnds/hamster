package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.RejectedDataPayload;
import com.google.gson.annotations.SerializedName;

public class AssetRejectedResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private RejectedDataPayload data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public RejectedDataPayload getData() {
        return data;
    }
}
