package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateAssetRequest {

    // --- Dari AssetInfoFragment ---
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
    @SerializedName("parentId")
    private String parentId;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("subcategoryId")
    private String subcategoryId;
    @SerializedName("brandId")
    private String brandId;

    // --- Dari AssetLocationFragment ---
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

    // --- Dari AssetMaintenanceFragment ---
    @SerializedName("vendorId")
    private String vendorId;
    private Long procurementDate;
    private Long warrantyExpirationDate;
    private Double purchasePrice;
    private String poNumber;
    private String invoiceNumber;
    private Double depreciation;
    private Double depreciationValue;
    private Long depreciationStartDate;
    private Integer depreciationDurationMonth;

    private List<String> keepSerialNumberPhotos;
    private List<String> keepAssetPhotos;


    // Getter

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

    public String getDescription() {
        return description;
    }

    public Integer getTotal() {
        return total;
    }

    public String getUnit() {
        return unit;
    }

    public String getCondition() {
        return condition;
    }

    public String getOwnership() {
        return ownership;
    }

    public String getParentId() {
        return parentId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public String getBrandId() {
        return brandId;
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

    public String getVendorId() {
        return vendorId;
    }

    public Long getProcurementDate() {
        return procurementDate;
    }

    public Long getWarrantyExpirationDate() {
        return warrantyExpirationDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
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

    // --- Setter untuk semua field ---

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
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setSubRoomId(String subRoomId) { this.subRoomId = subRoomId; }
    public void setResponsibleDivisionId(String responsibleDivisionId) { this.responsibleDivisionId = responsibleDivisionId; }
    public void setResponsibleWorkingUnitId(String responsibleWorkingUnitId) { this.responsibleWorkingUnitId = responsibleWorkingUnitId; }
    public void setResponsibleUserId(String responsibleUserId) { this.responsibleUserId = responsibleUserId; }
    public void setVendorId(String vendorId) { this.vendorId = vendorId; }
    public void setProcurementDate(Long procurementDate) { this.procurementDate = procurementDate; }
    public void setWarrantyExpirationDate(Long warrantyExpirationDate) { this.warrantyExpirationDate = warrantyExpirationDate; }
    public void setPurchasePrice(Double purchasePrice) { this.purchasePrice = purchasePrice; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setDepreciation(Double depreciation) { this.depreciation = depreciation; }
    public void setDepreciationValue(Double depreciationValue) { this.depreciationValue = depreciationValue; }
    public void setDepreciationStartDate(Long depreciationStartDate) { this.depreciationStartDate = depreciationStartDate; }
    public void setDepreciationDurationMonth(Integer depreciationDurationMonth) { this.depreciationDurationMonth = depreciationDurationMonth; }

    public List<String> getKeepSerialNumberPhotos() { return keepSerialNumberPhotos; }
    public void setKeepSerialNumberPhotos(List<String> keepSerialNumberPhotos) { this.keepSerialNumberPhotos = keepSerialNumberPhotos; }
    public List<String> getKeepAssetPhotos() { return keepAssetPhotos; }
    public void setKeepAssetPhotos(List<String> keepAssetPhotos) { this.keepAssetPhotos = keepAssetPhotos; }

}