package com.example.hamster.data.model;

import java.io.Serializable;

public class Asset implements Serializable {
    private String id;
    private String code;
    private String name;
    private String type;
    private String status;
    private String condition;
    private AssetCategory category;
    private AssetSubcategory subcategory;
    private Brand brand;
    private Room room;

    // Getters
    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getCondition() { return condition; }
    public AssetCategory getCategory() { return category; }
    public AssetSubcategory getSubcategory() { return subcategory; }
    public Brand getBrand() { return brand; }
    public Room getRoom() { return room; }
}