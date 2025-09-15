package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;

public class AssetApprovalTransaction implements Serializable {
    private String id;
    private String assetId;
    private int step;
    private String approverId;
    private String divisionId;
    private String positionId;
    private String status;
    private String remarks;
    private Date approvedAt;
    private Date createdAt;
    private Date updatedAt;

    private Asset asset;
    private User approver;
    private Division division;
    private Position position;

    // Getters
    public String getId() {
        return id;
    }

    public String getAssetId() {
        return assetId;
    }

    public int getStep() {
        return step;
    }

    public String getApproverId() {
        return approverId;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public String getPositionId() {
        return positionId;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public Date getApprovedAt() {
        return approvedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Asset getAsset() {
        return asset;
    }

    public User getApprover() {
        return approver;
    }

    public Division getDivision() {
        return division;
    }

    public Position getPosition() {
        return position;
    }
}