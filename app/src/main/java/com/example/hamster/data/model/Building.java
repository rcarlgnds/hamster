package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class Building implements Serializable {
    private String id;
    private String name;
    private String code;
    private String hospitalId;
    private Hospital hospital;
    private List<Floor> floors;

    // Getters
    public String getName() { return name; }
    public Hospital getHospital() { return hospital; }
}