package com.example.hamster.data.model.request;

public class UpdateAssetSubCategoryRequest {
    private String name;
    private String code;
    private String assetCategoryId;
    private String description;

    public UpdateAssetSubCategoryRequest(String name, String code, String assetCategoryId, String description) {
        this.name = name;
        this.code = code;
        this.assetCategoryId = assetCategoryId;
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
    public String getAssetCategoryId() {
        return assetCategoryId;
    }
    public void setAssetCategoryId(String assetCategoryId) {
        this.assetCategoryId = assetCategoryId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

