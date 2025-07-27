package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateAssetRequest {

    // --- Basic Info ---
    private String code;
    private String code2;
    private String code3;
    private String name;
    private String type;
    private String serialNumber;
    private String description;
    private Integer total;
    private String unit;
    private String condition;
    private String ownership;

    // --- Relational IDs ---
    @SerializedName("parentId")
    private String parentId;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("subcategoryId")
    private String subcategoryId;
    @SerializedName("brandId")
    private String brandId;
    @SerializedName("vendorId")
    private String vendorId;
    @SerializedName("roomId")
    private String roomId;
    @SerializedName("subRoomId")
    private String subRoomId;
    @SerializedName("responsibleDivisionId")
    private String responsibleDivisionId;
    @SerializedName("responsibleWorkingUnitId")
    private String responsibleWorkingUnitId;
    @SerializedName("responsibleUserId")
    private String responsibleUserId;

    // --- Procurement & Financial Info ---
    private Long procurementDate;
    private Long warrantyExpirationDate;
    private Double purchasePrice;
    private String poNumber;
    private String invoiceNumber;
    private Double depreciation;
    private Double depreciationValue;
    private Long depreciationStartDate;
    private Integer depreciationDurationMonth;


    // --- Setters ---
    public void setCode(String code) { this.code = code; }
    public void setCode2(String code2) { this.code2 = code2; }
    public void setCode3(String code3) { this.code3 = code3; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public void setDescription(String description) { this.description = description; }
    public void setTotal(Integer total) { this.total = total; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCondition(String condition) { this.condition = condition; }
    public void setOwnership(String ownership) { this.ownership = ownership; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setSubcategoryId(String subcategoryId) { this.subcategoryId = subcategoryId; }
    public void setBrandId(String brandId) { this.brandId = brandId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setSubRoomId(String subRoomId) { this.subRoomId = subRoomId; }
    public void setResponsibleDivisionId(String responsibleDivisionId) { this.responsibleDivisionId = responsibleDivisionId; }
    public void setResponsibleWorkingUnitId(String responsibleWorkingUnitId) { this.responsibleWorkingUnitId = responsibleWorkingUnitId; }
    public void setResponsibleUserId(String responsibleUserId) { this.responsibleUserId = responsibleUserId; }
    public void setProcurementDate(Long procurementDate) { this.procurementDate = procurementDate; }
    public void setWarrantyExpirationDate(Long warrantyExpirationDate) { this.warrantyExpirationDate = warrantyExpirationDate; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setDepreciation(Double depreciation) { this.depreciation = depreciation; }
    public void setDepreciationValue(Double depreciationValue) { this.depreciationValue = depreciationValue; }
    public void setDepreciationStartDate(Long depreciationStartDate) { this.depreciationStartDate = depreciationStartDate; }
    public void setDepreciationDurationMonth(Integer depreciationDurationMonth) { this.depreciationDurationMonth = depreciationDurationMonth; }
}