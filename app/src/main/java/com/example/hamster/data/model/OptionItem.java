package com.example.hamster.data.model;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class OptionItem implements Serializable {
    private String id;
    private String name;

    public OptionItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}