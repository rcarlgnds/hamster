package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class AssetSubCategory implements Serializable {
    private String id;
    private String name;
    private String code;
    private String categoryId;
    private AssetCategory category;
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

    public String getCategoryId() {
        return categoryId;
    }

    public AssetCategory getCategory() {
        return category;
    }

    public List<Asset> getAssets() {
        return assets;
    }
}