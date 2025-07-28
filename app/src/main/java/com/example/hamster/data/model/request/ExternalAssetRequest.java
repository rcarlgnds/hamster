package com.example.hamster.data.model.request;

public class ExternalAssetRequest {
    private String name;
    private String code;
    private String categoryId;
    private String vendorId;
    private String brandId;
    private String serialNumber;
    private String description;

    public ExternalAssetRequest(String name, String code, String categoryId, String vendorId, String brandId, String serialNumber, String description) {
        this.name = name;
        this.code = code;
        this.categoryId = categoryId;
        this.vendorId = vendorId;
        this.brandId = brandId;
        this.serialNumber = serialNumber;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
    public String getVendorId() {
        return vendorId;
    }
    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }
    public String getBrandId() {
        return brandId;
    }
    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

