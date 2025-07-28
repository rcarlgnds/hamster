package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Asset implements Serializable {
    private String id;
    private String code;
    private String code2;
    private String code3;
    private String name;
    private String type;
    private String serialNumber;
    private String ownership;
    private String categoryId;
    private String subcategoryId;
    private Integer total;
    private String unit;
    private String description;
    private String status;
    private String brandId;
    private String condition;
    private String roomId;
    private String subRoomId;
    private String responsibleDivisionId;
    private String responsibleWorkingUnitId;
    private String responsibleUserId;
    private Date procurementDate;
    private Float purchasePrice;
    private String vendorId;
    private String poNumber;
    private String invoiceNumber;
    private Date warrantyExpirationDate;
    private Float depreciation;
    private Float depreciationValue;
    private Date depreciationStartDate;
    private Integer depreciationDurationMonth;
    private String parentId;
    private Date deletedAt;
    private String deletedBy;
    private String createdVia;
    private Date createdAt;
    private Date updatedAt;

    // Relasi
    private AssetCategory category;
    private AssetSubcategory subcategory;
    private Brand brand;
    private Room room;
    private SubRoom subRoom;
    private Division responsibleDivision;
    private WorkingUnit responsibleWorkingUnit;
    private User responsibleUser;
    private Vendor vendor;
    private Asset parent;
    private List<Asset> children;
    private List<AssetMediaFile> mediaFiles;
    private List<AssetApprovalTransaction> approvalTransactions;

    // Getters
    public String getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public AssetCategory getCategory() { return category; }
    public AssetSubcategory getSubcategory() { return subcategory; }
    public Brand getBrand() { return brand; }
    public Room getRoom() { return room; }
}