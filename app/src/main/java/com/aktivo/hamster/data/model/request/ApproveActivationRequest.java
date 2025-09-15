package com.aktivo.hamster.data.model.request;

import com.google.gson.annotations.SerializedName;

public class ApproveActivationRequest {

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("action")
    private String action;

    @SerializedName("remarks")
    private String remarks;

    public ApproveActivationRequest(String transactionId, String action, String remarks) {
        this.transactionId = transactionId;
        this.action = action;
        this.remarks = remarks;
    }

}