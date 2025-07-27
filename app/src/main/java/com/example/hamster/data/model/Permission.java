package com.example.hamster.data.model;

import java.io.Serializable;

public class Permission implements Serializable {
    private String id;
    private String key;
    private String title;
    private String description;

    public String getId() { return id; }
    public String getKey() { return key; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}