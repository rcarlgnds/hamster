package com.aktivo.hamster.data.model.request;

public class UpdateRolePermissionRequest {
    private String divisionId;
    private String positionId;
    private String permissions;

    public UpdateRolePermissionRequest(String divisionId, String positionId, String permissions) {
        this.divisionId = divisionId;
        this.positionId = positionId;
        this.permissions = permissions;
    }

    public String getDivisionId() {
        return divisionId;
    }
    public void setDivisionId(String divisionId) {
        this.divisionId = divisionId;
    }
    public String getPositionId() {
        return positionId;
    }
    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }
    public String getPermissions() {
        return permissions;
    }
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}

