package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.ApprovalDataPayload;
import com.google.gson.annotations.SerializedName;

public class PendingApprovalsResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private ApprovalDataPayload data;


    public boolean isSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }

    public ApprovalDataPayload getData() {
        return data;
    }
}