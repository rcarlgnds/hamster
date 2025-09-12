package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AssetRejected implements Serializable {
    @SerializedName("id")
    private String id;
    private String assetCode;
    private String assetName;
    private String rejectedByPosition;
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

    public String getStatus() {
        return status;
    }
}
