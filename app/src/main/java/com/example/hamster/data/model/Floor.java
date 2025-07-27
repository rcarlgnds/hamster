package com.example.hamster.data.model;

import java.io.Serializable;

public class Floor implements Serializable {
    private String id;
    private String name;
    private Building building;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Building getBuilding() {
        return building;
    }
}