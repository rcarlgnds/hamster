package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class SubRoom implements Serializable {
    private String id;
    private String code;
    private String name;
    private String roomId;
    private Room room;
    private List<Asset> assets;

    // Getters
    public String getName() { return name; }
}