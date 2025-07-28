package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    private String id;
    private String code;
    private String name;
    private String divisionId;
    private String floorId;
    private Floor floor;
    private Division division;
    private List<SubRoom> subRooms;
    private List<Asset> assets;
    private List<User> users;

    // Getters
    public String getName() { return name; }
    public Floor getFloor() { return floor; }
}