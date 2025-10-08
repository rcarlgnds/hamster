package com.aktivo.hamster.data.model.request;

import com.google.gson.annotations.SerializedName;

public class ScheduleInstallationRequest {

    @SerializedName("assetId")
    private String assetId;

    @SerializedName("vendorId")
    private String vendorId;

    @SerializedName("scheduledAt")
    private long scheduledAt;

    @SerializedName("alsoScheduleTraining")
    private boolean alsoScheduleTraining;

    public ScheduleInstallationRequest(String assetId, String vendorId, long scheduledAt, boolean alsoScheduleTraining) {
        this.assetId = assetId;
        this.vendorId = vendorId;
        this.scheduledAt = scheduledAt;
        this.alsoScheduleTraining = alsoScheduleTraining;
    }
}