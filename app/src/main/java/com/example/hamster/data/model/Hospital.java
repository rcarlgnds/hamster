package com.example.hamster.data.model;

import java.io.Serializable;

public class Hospital implements Serializable {
    private String id;
    private String name;
    private String city;
    private String location;

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getLocation() { return location; }
}