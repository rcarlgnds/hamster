package com.example.hamster.data.model.request;

public class ApproveActivationRequest {
    private String assetId;
    private String approverId;
    private boolean approved;
    private String notes;

    public ApproveActivationRequest(String assetId, String approverId, boolean approved, String notes) {
        this.assetId = assetId;
        this.approverId = approverId;
        this.approved = approved;
        this.notes = notes;
    }

    public String getAssetId() {
        return assetId;
    }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    public String getApproverId() {
        return approverId;
    }
    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }
    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

