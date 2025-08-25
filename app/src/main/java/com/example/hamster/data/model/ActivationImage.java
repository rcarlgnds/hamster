package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class ActivationImage {

    @SerializedName("url")
    private String url;

    public String getUrl() {
        return url;
    }
}