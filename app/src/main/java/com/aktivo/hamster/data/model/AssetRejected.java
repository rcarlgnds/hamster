package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssetRejected implements Serializable {
    @SerializedName("id")
    private String id;
    private String assetCode;
    private String assetName;
    private String rejectedByPosition;
    private String rejectedAt;
    private String rejectedAtStep;
    private String status;


    public String getId() {
        return id;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getRejectedByPosition() {
        return rejectedByPosition;
    }

    public String getRejectedAt() {
        return rejectedAt;
    }

    public String getRejectedAtStep() {
        return rejectedAtStep;
    }

    public String getStatus() {
        return status;
    }
}
