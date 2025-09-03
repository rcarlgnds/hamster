package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class ActivationDetailData {

    @SerializedName("startedAt")
    private long startedAt;

    @SerializedName("image")
    private ActivationImage image;


    public long getStartedAt() {
        return startedAt;
    }

    public ActivationImage getImage() {
        return image;
    }
}