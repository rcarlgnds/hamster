package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Asset;
import com.google.gson.annotations.SerializedName;

public class AssetByCodeResponse {

    @SerializedName("data")
    private Asset data;

    public Asset getData() {
        return data;
    }
}