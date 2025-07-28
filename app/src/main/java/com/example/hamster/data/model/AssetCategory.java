package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AssetCategory implements Serializable {
    private String id;
    private String name;
    private String code;
    private Date createdAt;
    private Date updatedAt;
    private List<AssetSubCategory> subcategories;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public List<AssetSubCategory> getSubcategories() {
        return subcategories;
    }

    public List<Asset> getAssets() {
        return assets;
    }
}