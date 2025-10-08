package com.aktivo.hamster.data.model;

import com.google.gson.annotations.SerializedName;
public class TransactionDetails {
    @SerializedName("id")
    private String id;

    public String getId() {
        return id;
    }
}