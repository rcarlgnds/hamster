package com.example.hamster.data.model;

import java.io.Serializable;

public class Position implements Serializable  {
    private String id;
    private String name;
    private boolean isApproval;

    public String getId() { return id; }
    public String getName() { return name; }
    public boolean isApproval() { return isApproval; }
}