package com.aktivo.hamster.data.model.request;

import com.google.gson.annotations.SerializedName;

public class ScheduleTrainingRequest {

    @SerializedName("assetId")
    private String assetId;

    @SerializedName("vendorId")
    private String vendorId;

    @SerializedName("scheduledAt")
    private long scheduledAt;

    public ScheduleTrainingRequest(String assetId, String vendorId, long scheduledAt) {
        this.assetId = assetId;
        this.vendorId = vendorId;
        this.scheduledAt = scheduledAt;
    }
}