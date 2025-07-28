package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Asset;
import java.util.List;

public class AssetActivationResponse {
    private String id;
    private String assetId;
    private String status;
    private List<String> approverIds;
    private String createdAt;
    private String updatedAt;

    public AssetActivationResponse(String id, String assetId, String status, List<String> approverIds, String createdAt, String updatedAt) {
        this.id = id;
        this.assetId = assetId;
        this.status = status;
        this.approverIds = approverIds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
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
    public List<String> getApproverIds() {
        return approverIds;
    }
    public void setApproverIds(List<String> approverIds) {
        this.approverIds = approverIds;
    }
    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}

