package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class Asset implements Serializable {
    private long createdAt;
    private long updatedAt;
    private String id;
    private String code;
    private String code2;
    private String code3;
    private String name;
    private String type;
    private String serialNumber;
    private String description;
    private int total;
    private String unit;
    private String status;
    private String condition;
    private String ownership;
    private AssetCategory category;
    private AssetSubcategory subcategory;
    private Brand brand;
    private Vendor vendor;
    private Room room;
    private SubRoom subRoom;
    private Division responsibleDivision;
    private WorkingUnit responsibleWorkingUnit;
    private List<Object> children;

    // Getters
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public String getId() { return id; }
    public String getCode() { return code; }
    public String getCode2() { return code2; }
    public String getCode3() { return code3; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getSerialNumber() { return serialNumber; }
    public String getDescription() { return description; }
    public int getTotal() { return total; }
    public String getUnit() { return unit; }
    public String getStatus() { return status; }
    public String getCondition() { return condition; }
    public String getOwnership() { return ownership; }
    public AssetCategory getCategory() { return category; }
    public AssetSubcategory getSubcategory() { return subcategory; }
    public Brand getBrand() { return brand; }
    public Vendor getVendor() { return vendor; }
    public Room getRoom() { return room; }
    public SubRoom getSubRoom() { return subRoom; }
    public Division getResponsibleDivision() { return responsibleDivision; }
    public WorkingUnit getResponsibleWorkingUnit() { return responsibleWorkingUnit; }
    public List<Object> getChildren() { return children; }
}