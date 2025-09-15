package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Control implements Serializable {

    @SerializedName("key")
    private String key;

    @SerializedName("category")
    private String category;

    @SerializedName("name")
    private String name;

    @SerializedName("step")
    private int step;

    @SerializedName("stepName")
    private String stepName;

    @SerializedName("description")
    private String description;

    @SerializedName("canApprove")
    private boolean canApprove;

    // Getters
    public String getKey() {
        return key;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public int getStep() {
        return step;
    }

    public String getStepName() {
        return stepName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCanApprove() {
        return canApprove;
    }
}