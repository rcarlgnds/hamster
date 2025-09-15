package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Hospital implements Serializable {
    private String id;
    private String name;
    private String city;
    private String location;
    private String code;
    private String address;
    private String contact;
    private Date deletedAt;
    private String deletedBy;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private List<User> users;
    private List<Building> buildings;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getCode() {
        return code;
    }

    public String getAddress() {
        return address;
    }

    public String getContact() {
        return contact;
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

    public List<Building> getBuildings() {
        return buildings;
    }
}