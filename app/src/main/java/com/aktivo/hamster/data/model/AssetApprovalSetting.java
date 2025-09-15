package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;

public class AssetApprovalSetting implements Serializable {
    private String id;
    private int step;
    private String divisionId;
    private String positionId;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private Division division;
    private Position position;

    // Getters
    public String getId() {
        return id;
    }

    public int getStep() {
        return step;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public String getPositionId() {
        return positionId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Division getDivision() {
        return division;
    }

    public Position getPosition() {
        return position;
    }
}