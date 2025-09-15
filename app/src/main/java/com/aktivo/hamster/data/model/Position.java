package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Position implements Serializable {
    private String id;
    private String name;
    private Boolean isApproval;
    private Date deletedAt;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private List<User> users;
    private List<RolePermission> rolePermissions;
    private List<AssetApprovalSetting> approvalSettings;
    private List<AssetApprovalTransaction> approvalTransactions;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Boolean getApproval() {
        return isApproval;
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

    public List<User> getUsers() {
        return users;
    }

    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public List<AssetApprovalSetting> getApprovalSettings() {
        return approvalSettings;
    }

    public List<AssetApprovalTransaction> getApprovalTransactions() {
        return approvalTransactions;
    }
}