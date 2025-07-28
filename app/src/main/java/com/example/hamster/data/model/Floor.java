package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class Floor implements Serializable {
    private String id;
    private String code;
    private String name;
    private String buildingId;
    private Building building;
    private List<Room> rooms;

    // Getters
    public String getName() { return name; }
    public Building getBuilding() { return building; }
}