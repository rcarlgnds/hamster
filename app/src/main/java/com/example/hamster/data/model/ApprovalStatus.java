package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class ApprovalStatus {

    @SerializedName("step")
    private int step;

    @SerializedName("divisionName")
    private String divisionName;

    @SerializedName("positionName")
    private String positionName;

    @SerializedName("status")
    private String status;

    // Getters
    public int getStep() {
        return step;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public String getPositionName() {
        return positionName;
    }

    public String getStatus() {
        return status;
    }
}