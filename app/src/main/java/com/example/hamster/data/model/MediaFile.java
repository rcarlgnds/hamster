package com.example.hamster.data.model;

import java.io.Serializable;
import java.util.Date;

public class MediaFile implements Serializable {
    private String id;
    private String filename;
    private String originalName;
    private String mimeType;
    private int size;
    private String path;
    private String url;
    private Date createdAt;
    private Date updatedAt;

    // Getters
    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getSize() {
        return size;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}