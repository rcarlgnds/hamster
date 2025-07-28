package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Room implements Serializable {
    private String id;
    private String code;
    private String name;
    private String divisionId;
    private String classType;
    private Integer bedCount;
    private Float electricalPower;
    private boolean isActive;
    private String floorId;
    private Date deletedAt;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;


    // Relasi
    private Floor floor;
    private Division division;
    private List<SubRoom> subRooms;
    private List<Asset> assets;
    private List<User> users;

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

    public String getDivisionId() {
        return divisionId;
    }

    public String getClassType() {
        return classType;
    }

    public Integer getBedCount() {
        return bedCount;
    }

    public Float getElectricalPower() {
        return electricalPower;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getFloorId() {
        return floorId;
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

    public Floor getFloor() {
        return floor;
    }

    public Division getDivision() {
        return division;
    }

    public List<SubRoom> getSubRooms() {
        return subRooms;
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public List<User> getUsers() {
        return users;
    }
}