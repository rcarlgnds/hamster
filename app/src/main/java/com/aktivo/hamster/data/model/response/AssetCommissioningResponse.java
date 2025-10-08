package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.CommissioningData;
import com.google.gson.annotations.SerializedName;

public class AssetCommissioningResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private CommissioningData data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public CommissioningData getData() {
        return data;
    }
}