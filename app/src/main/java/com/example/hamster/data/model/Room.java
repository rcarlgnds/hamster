package com.example.hamster.data.model;

import java.io.Serializable;

public class Room implements Serializable {
    private String id;
    private String name;
    private String code;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}