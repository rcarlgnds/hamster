package com.aktivo.hamster.data.model.response;

public class AssetApprovalStatusResponse {
    private String assetId;
    private String status;
    private String lastApproverId;
    private String lastUpdatedAt;

    public AssetApprovalStatusResponse(String assetId, String status, String lastApproverId, String lastUpdatedAt) {
        this.assetId = assetId;
        this.status = status;
        this.lastApproverId = lastApproverId;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getAssetId() {
        return assetId;
    }
    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getLastApproverId() {
        return lastApproverId;
    }
    public void setLastApproverId(String lastApproverId) {
        this.lastApproverId = lastApproverId;
    }
    public String getLastUpdatedAt() {
        return lastUpdatedAt;
    }
    public void setLastUpdatedAt(String lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }
}

