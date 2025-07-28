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
    private Long procurementDate;
    private Double purchasePrice;
    private String vendorId;
    private String poNumber;
    private String invoiceNumber;
    private Long warrantyExpirationDate;
    private Double depreciation;
    private Double depreciationValue;
    private Long depreciationStartDate;
    private Integer depreciationDurationMonth;
    private String parentId;
    private Long deletedAt;
    private String deletedBy;
    private String createdVia;
    private Long createdAt;
    private Long updatedAt;

    // Relasi
    private AssetCategory category;
    private AssetSubCategory subcategory;
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
    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getCode2() {
        return code2;
    }

    public String getCode3() {
        return code3;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getOwnership() {
        return ownership;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public Integer getTotal() {
        return total;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getCondition() {
        return condition;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getSubRoomId() {
        return subRoomId;
    }

    public String getResponsibleDivisionId() {
        return responsibleDivisionId;
    }

    public String getResponsibleWorkingUnitId() {
        return responsibleWorkingUnitId;
    }

    public String getResponsibleUserId() {
        return responsibleUserId;
    }

    public Long getProcurementDate() {
        return procurementDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public Long getWarrantyExpirationDate() {
        return warrantyExpirationDate;
    }

    public Double getDepreciation() {
        return depreciation;
    }

    public Double getDepreciationValue() {
        return depreciationValue;
    }

    public Long getDepreciationStartDate() {
        return depreciationStartDate;
    }

    public Integer getDepreciationDurationMonth() {
        return depreciationDurationMonth;
    }

    public String getParentId() {
        return parentId;
    }

    public Long getDeletedAt() {
        return deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public String getCreatedVia() {
        return createdVia;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public AssetSubCategory getSubcategory() {
        return subcategory;
    }

    public Brand getBrand() {
        return brand;
    }

    public Room getRoom() {
        return room;
    }

    public SubRoom getSubRoom() {
        return subRoom;
    }

    public Division getResponsibleDivision() {
        return responsibleDivision;
    }

    public WorkingUnit getResponsibleWorkingUnit() {
        return responsibleWorkingUnit;
    }

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public Asset getParent() {
        return parent;
    }

    public List<Asset> getChildren() {
        return children;
    }

    public List<AssetMediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public List<AssetApprovalTransaction> getApprovalTransactions() {
        return approvalTransactions;
    }
}