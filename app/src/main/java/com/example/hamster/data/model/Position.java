package com.example.hamster.data.model;

import java.io.Serializable;

public class Position implements Serializable {
    private String id;
    private String name;
    private Boolean isApproval;

    // Getters
    public String getName() { return name; }
}