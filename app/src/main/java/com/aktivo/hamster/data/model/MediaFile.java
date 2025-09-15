package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class MediaFile {
    @SerializedName("id")
    private String id;
    @SerializedName("filename")
    private String filename;
    @SerializedName("originalName")
    private String originalName;
    @SerializedName("mimeType")
    private String mimeType;
    @SerializedName("size")
    private Long size;
    @SerializedName("url")
    private String url;
    @SerializedName("path")
    private String path;

    // Getters
    public String getId() { return id; }
    public String getFilename() { return filename; }
    public String getOriginalName() { return originalName; }
    public String getMimeType() { return mimeType; }
    public Long getSize() { return size; }
    public String getUrl() { return url; }
    public String getPath() { return path; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setFilename(String filename) { this.filename = filename; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public void setSize(Long size) { this.size = size; }
    public void setUrl(String url) { this.url = url; }
    public void setPath(String path) { this.path = path; }
}