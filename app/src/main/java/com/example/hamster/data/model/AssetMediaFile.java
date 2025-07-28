package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;

public class AssetMediaFile implements Serializable {
    private String id;
    private String assetId;
    private String mediaFileId;
    private String type;
    private String name;
    private Date createdAt;
    private Date updatedAt;

    private Asset asset;
    private MediaFile mediaFile;

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public MediaFile getMediaFile() { return mediaFile; }
}