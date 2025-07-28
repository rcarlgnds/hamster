package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String employeeId;
    private String prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String password;
    private String email;
    private String phoneNumber;
    private String divisionId;
    private String positionId;
    private String hospitalId;
    private String workingUnitId;
    private String roomId;
    private Date deletedAt;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private Division division;
    private Position position;
    private Hospital hospital;
    private WorkingUnit workingUnit;
    private Room room;
    private List<Asset> responsibleAssets;
    private List<AssetApprovalTransaction> approvalTransactions;
    private List<Notification> notifications;

    // Getters
    public String getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDivisionId() {
        return divisionId;
    }

    public String getPositionId() {
        return positionId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getWorkingUnitId() {
        return workingUnitId;
    }

    public String getRoomId() {
        return roomId;
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

    public Division getDivision() {
        return division;
    }

    public Position getPosition() {
        return position;
    }

    public Hospital getHospital() {
        return hospital;
    }

    public WorkingUnit getWorkingUnit() {
        return workingUnit;
    }

    public Room getRoom() {
        return room;
    }

    public List<Asset> getResponsibleAssets() {
        return responsibleAssets;
    }

    public List<AssetApprovalTransaction> getApprovalTransactions() {
        return approvalTransactions;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}