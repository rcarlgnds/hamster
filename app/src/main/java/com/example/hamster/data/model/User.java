package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String employeeId;
    private String prefix;
    private String firstName;
    private String middleName;
    private String lastName;
    private String suffix;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Division division;
    private Position position;
    private Hospital hospital;
    private WorkingUnit workingUnit;
    private List<Permission> permissions;
    private long createdAt;
    private long updatedAt;
    private Room room;




    public String getId() { return id; }
    public String getEmployeeId() { return employeeId; }
    public String getPrefix() { return prefix; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public String getSuffix() { return suffix; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public Division getDivision() { return division; }
    public Position getPosition() { return position; }
    public Hospital getHospital() { return hospital; }
    public WorkingUnit getWorkingUnit() { return workingUnit; }
    public List<Permission> getPermissions() { return permissions; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public Room getRoom() { return room; }
}