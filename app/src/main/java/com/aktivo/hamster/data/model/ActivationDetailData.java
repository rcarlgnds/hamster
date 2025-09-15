package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class ActivationDetailData {

    @SerializedName("startedAt")
    private long startedAt;

    @SerializedName("startedBy")
    private ActivationStartedBy startedBy;

    @SerializedName("image")
    private ActivationImage image;


    public long getStartedAt() {
        return startedAt;
    }

    public ActivationStartedBy getStartedBy() {
        return startedBy;
    }

    public ActivationImage getImage() {
        return image;
    }
}