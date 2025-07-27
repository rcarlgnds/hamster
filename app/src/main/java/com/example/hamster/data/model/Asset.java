package com.example.hamster.data.model;

import java.io.Serializable;

public class Asset implements Serializable {
    private String id, code, code2, code3, name, type, serialNumber, description, unit, status, condition, ownership;
    private int total;
    private Long procurementDate, warrantyExpirationDate;
    private AssetCategory category;
    private AssetSubcategory subcategory;
    private Brand brand;
    private Vendor vendor;
    private Room room;
    private SubRoom subRoom;
    private Division responsibleDivision;
    private WorkingUnit responsibleWorkingUnit;

    // Getters
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
    public Long getProcurementDate() { return procurementDate; }
    public Long getWarrantyExpirationDate() { return warrantyExpirationDate; }
    public AssetCategory getCategory() { return category; }
    public AssetSubcategory getSubcategory() { return subcategory; }
    public Brand getBrand() { return brand; }
    public Vendor getVendor() { return vendor; }
    public Room getRoom() { return room; }
    public SubRoom getSubRoom() { return subRoom; }
    public Division getResponsibleDivision() { return responsibleDivision; }
    public WorkingUnit getResponsibleWorkingUnit() { return responsibleWorkingUnit; }
}