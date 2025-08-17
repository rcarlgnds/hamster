package com.example.hamster.data.model;

import java.util.ArrayList;
import java.util.List;

public class UpdateAssetRequest {
    private String code;
    private String name;
    private String aliasNameTeramedik;
    private String aliasNameHamster;
    private String ownership;
    private String categoryId;
    private String subcategoryId;
    private String brandId;
    private String condition;
    private String roomId;
    private String subRoomId;
    private String responsibleDivisionId;
    private String responsibleWorkingUnitId;
    private String responsibleUserId;
    private String vendorId;
    private String type;
    private String serialNumber;
    private String code2;
    private String code3;
    private String parentId;
    private String description;
    private Integer total;
    private String unit;
    private Long procurementDate;
    private Long warrantyExpirationDate;
    private Double purchasePrice;
    private String poNumber;
    private String invoiceNumber;
    private Double depreciation;
    private Double depreciationValue;
    private Long depreciationStartDate;
    private Long effectiveUsageDate;

    private Integer depreciationDurationMonth;

    // --- Fields untuk menyimpan ID foto/dokumen yang akan dipertahankan ---
    private List<String> keepPoDocuments;
    private List<String> keepInvoiceDocuments;
    private List<String> keepWarrantyDocuments;
    private List<String> keepSerialNumberPhotos;
    private List<String> keepAssetPhotos;
    private List<String> keepLicenseDocuments;
    private List<String> keepUserManualDocuments;
    private List<String> keepCustomDocuments;

    // --- Constructor ---
    public UpdateAssetRequest() {
        this.keepPoDocuments = new ArrayList<>();
        this.keepInvoiceDocuments = new ArrayList<>();
        this.keepWarrantyDocuments = new ArrayList<>();
        this.keepSerialNumberPhotos = new ArrayList<>();
        this.keepAssetPhotos = new ArrayList<>();
        this.keepLicenseDocuments = new ArrayList<>();
        this.keepUserManualDocuments = new ArrayList<>();
        this.keepCustomDocuments = new ArrayList<>();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAliasNameTeramedik() {
        return aliasNameTeramedik;
    }

    public void setAliasNameTeramedik(String aliasNameTeramedik) {
        this.aliasNameTeramedik = aliasNameTeramedik;
    }

    public String getAliasNameHamster() {
        return aliasNameHamster;
    }

    public void setAliasNameHamster(String aliasNameHamster) {
        this.aliasNameHamster = aliasNameHamster;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId = subcategoryId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSubRoomId() {
        return subRoomId;
    }

    public void setSubRoomId(String subRoomId) {
        this.subRoomId = subRoomId;
    }

    public String getResponsibleDivisionId() {
        return responsibleDivisionId;
    }

    public void setResponsibleDivisionId(String responsibleDivisionId) {
        this.responsibleDivisionId = responsibleDivisionId;
    }

    public String getResponsibleWorkingUnitId() {
        return responsibleWorkingUnitId;
    }

    public void setResponsibleWorkingUnitId(String responsibleWorkingUnitId) {
        this.responsibleWorkingUnitId = responsibleWorkingUnitId;
    }

    public String getResponsibleUserId() {
        return responsibleUserId;
    }

    public void setResponsibleUserId(String responsibleUserId) {
        this.responsibleUserId = responsibleUserId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public String getCode3() {
        return code3;
    }

    public void setCode3(String code3) {
        this.code3 = code3;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Long getProcurementDate() {
        return procurementDate;
    }

    public void setProcurementDate(Long procurementDate) {
        this.procurementDate = procurementDate;
    }

    public Long getWarrantyExpirationDate() {
        return warrantyExpirationDate;
    }

    public void setWarrantyExpirationDate(Long warrantyExpirationDate) {
        this.warrantyExpirationDate = warrantyExpirationDate;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Double getDepreciation() {
        return depreciation;
    }

    public void setDepreciation(Double depreciation) {
        this.depreciation = depreciation;
    }

    public Double getDepreciationValue() {
        return depreciationValue;
    }

    public void setDepreciationValue(Double depreciationValue) {
        this.depreciationValue = depreciationValue;
    }

    public Long getDepreciationStartDate() {
        return depreciationStartDate;
    }

    public void setDepreciationStartDate(Long depreciationStartDate) {
        this.depreciationStartDate = depreciationStartDate;
    }

    public Long getEffectiveUsageDate() {
        return effectiveUsageDate;
    }

    public void setEffectiveUsageDate(Long effectiveUsageDate) {
        this.effectiveUsageDate = effectiveUsageDate;
    }

    public Integer getDepreciationDurationMonth() {
        return depreciationDurationMonth;
    }

    public void setDepreciationDurationMonth(Integer depreciationDurationMonth) {
        this.depreciationDurationMonth = depreciationDurationMonth;
    }

    public List<String> getKeepPoDocuments() {
        return keepPoDocuments;
    }

    public void setKeepPoDocuments(List<String> keepPoDocuments) {
        this.keepPoDocuments = keepPoDocuments;
    }

    public List<String> getKeepInvoiceDocuments() {
        return keepInvoiceDocuments;
    }

    public void setKeepInvoiceDocuments(List<String> keepInvoiceDocuments) {
        this.keepInvoiceDocuments = keepInvoiceDocuments;
    }

    public List<String> getKeepWarrantyDocuments() {
        return keepWarrantyDocuments;
    }

    public void setKeepWarrantyDocuments(List<String> keepWarrantyDocuments) {
        this.keepWarrantyDocuments = keepWarrantyDocuments;
    }

    public List<String> getKeepSerialNumberPhotos() {
        return keepSerialNumberPhotos;
    }

    public void setKeepSerialNumberPhotos(List<String> keepSerialNumberPhotos) {
        this.keepSerialNumberPhotos = keepSerialNumberPhotos;
    }

    public List<String> getKeepAssetPhotos() {
        return keepAssetPhotos;
    }

    public void setKeepAssetPhotos(List<String> keepAssetPhotos) {
        this.keepAssetPhotos = keepAssetPhotos;
    }

    public List<String> getKeepLicenseDocuments() {
        return keepLicenseDocuments;
    }

    public void setKeepLicenseDocuments(List<String> keepLicenseDocuments) {
        this.keepLicenseDocuments = keepLicenseDocuments;
    }

    public List<String> getKeepUserManualDocuments() {
        return keepUserManualDocuments;
    }

    public void setKeepUserManualDocuments(List<String> keepUserManualDocuments) {
        this.keepUserManualDocuments = keepUserManualDocuments;
    }

    public List<String> getKeepCustomDocuments() {
        return keepCustomDocuments;
    }

    public void setKeepCustomDocuments(List<String> keepCustomDocuments) {
        this.keepCustomDocuments = keepCustomDocuments;
    }
}