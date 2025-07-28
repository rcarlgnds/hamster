package com.example.hamster.data.model.request;

public class StartActivationRequest {
    private String assetId;
    private String initiatorId;
    private String notes;

    public StartActivationRequest(String assetId, String initiatorId, String notes) {
        this.assetId = assetId;
        this.initiatorId = initiatorId;
        this.notes = notes;
    }

    public String getAssetId() {
        return assetId;
    }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    public String getInitiatorId() {
        return initiatorId;
    }
    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}

