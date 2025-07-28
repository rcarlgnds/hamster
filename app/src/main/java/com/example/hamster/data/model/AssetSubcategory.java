package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class AssetSubcategory implements Serializable {
    private String id;
    private String name;
    private String code;
    private String categoryId;
    private AssetCategory category;
    private List<Asset> assets;

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
}