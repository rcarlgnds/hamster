package com.aktivo.hamster.data.model;

import java.io.Serializable;

public class Brand implements Serializable {
    private String id;
    private String name;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}