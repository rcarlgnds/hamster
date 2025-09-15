package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.Asset;
import com.google.gson.annotations.SerializedName;

public class AssetByCodeResponse {

    @SerializedName("data")
    private Asset data;

    public Asset getData() {
        return data;
    }
}