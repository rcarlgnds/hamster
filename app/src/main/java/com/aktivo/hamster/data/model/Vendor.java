package com.aktivo.hamster.data.model;

import java.io.Serializable;

public class Vendor implements Serializable {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}