package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Floor implements Serializable {
    private String id;
    private String code;
    private String name;
    private String floorPlanId;
    private String buildingId;
    private Date deletedAt;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private Building building;
    private MediaFile floorPlan;
    private List<Room> rooms;

    // Getters
    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getFloorPlanId() {
        return floorPlanId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Building getBuilding() {
        return building;
    }

    public MediaFile getFloorPlan() {
        return floorPlan;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}