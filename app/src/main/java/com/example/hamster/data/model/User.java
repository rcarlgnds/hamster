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
    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Division getDivision() { return division; }
    public Position getPosition() { return position; }
}