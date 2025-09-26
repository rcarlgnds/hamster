package com.aktivo.hamster.data.model;

import java.util.Date;
import java.util.List;

public class AssetUnit {
    private String id;
    private String name;
    private String code;
    private String subCategoryId;
    private AssetSubCategory subCategory;
    private List<Asset> assets;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public AssetSubCategory getSubCategory() {
        return subCategory;
    }

    public List<Asset> getAssets() {
        return assets;
    }
}
