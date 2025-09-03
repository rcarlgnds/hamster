package com.example.hamster.data.model;

import java.io.Serializable;

public class Unit implements Serializable {
    private String id;
    private String name;
    private String subcategoryId;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }
}