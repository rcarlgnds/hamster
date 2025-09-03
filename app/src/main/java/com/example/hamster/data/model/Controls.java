package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class Controls {

    @SerializedName("asset-activation-approval")
    private ControlAccess assetActivationApproval;

    public ControlAccess getAssetActivationApproval() {
        return assetActivationApproval;
    }

    public void setAssetActivationApproval(ControlAccess assetActivationApproval) {
        this.assetActivationApproval = assetActivationApproval;
    }
}