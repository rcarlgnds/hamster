package com.example.hamster.data.model;

import java.io.Serializable;

public class Building implements Serializable {
    private String id;
    private String name;
    private Hospital hospital;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Hospital getHospital() {
        return hospital;
    }
}