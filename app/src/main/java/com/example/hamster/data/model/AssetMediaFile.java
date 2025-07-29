// File: app/src/main/java/com/example/hamster/data/model/AssetMediaFile.java
package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class AssetMediaFile {
    @SerializedName("id")
    private String id; // Ini adalah ID record asosiasi (misal: c4988343-...)
    @SerializedName("assetId")
    private String assetId;
    @SerializedName("mediaFileId")
    private String mediaFileId; // Ini adalah ID file fisik (misal: 11a63851-...)
    @SerializedName("type")
    private String type;
    @SerializedName("name")
    private String name; // Untuk dokumen, ini mungkin nama kustom
    @SerializedName("createdAt")
    private String createdAt; // Atau Long jika Unix timestamp
    @SerializedName("updatedAt")
    private String updatedAt; // Atau Long jika Unix timestamp
    @SerializedName("mediaFile")
    private MediaFile mediaFile; // Objek mediaFile yang bersarang

    // Getters
    public String getId() { return id; }
    public String getAssetId() { return assetId; }
    public String getMediaFileId() { return mediaFileId; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public MediaFile getMediaFile() { return mediaFile; }

    public void setId(String id) { this.id = id; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public void setMediaFileId(String mediaFileId) { this.mediaFileId = mediaFileId; }
    public void setType(String type) { this.type = type; }
    public void setName(String name) { this.name = name; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public void setMediaFile(MediaFile mediaFile) { this.mediaFile = mediaFile; }
}