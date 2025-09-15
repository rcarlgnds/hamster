package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AssetActivationStatus {

    @SerializedName("assetId")
    private String assetId;

    @SerializedName("assetCode")
    private String assetCode;

    @SerializedName("assetName")
    private String assetName;

    @SerializedName("currentStep")
    private int currentStep;

    @SerializedName("totalSteps")
    private int totalSteps;

    @SerializedName("status")
    private String status;

    @SerializedName("approvals")
    private List<ApprovalStatus> approvals;

    // --- Getters ---
    public String getAssetId() {
        return assetId;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public String getStatus() {
        return status;
    }

    public List<ApprovalStatus> getApprovals() {
        return approvals;
    }
}