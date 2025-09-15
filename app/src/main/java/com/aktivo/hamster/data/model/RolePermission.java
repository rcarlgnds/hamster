package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;

public class RolePermission implements Serializable {
    private String id;
    private String divisionId;
    private String positionId;
    private String permissionId;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private Division division;
    private Position position;
    private Permission permission;

    // Getters
    public String getId() {
        return id;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public String getPositionId() {
        return positionId;
    }

    public String getPermissionId() {
        return permissionId;
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

    public Permission getPermission() {
        return permission;
    }
}