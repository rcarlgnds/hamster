package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class Hospital implements Serializable {
    private String id;
    private String name;
    private String city;
    private String location;
    private String code;
    private String address;
    private String contact;
    private List<Building> buildings;

    // Getters
    public String getName() { return name; }
}