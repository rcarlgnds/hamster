package com.example.hamster.data.model;

import java.io.Serializable;

public class OptionItem implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}