package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.AssetActivationStatus;
import com.google.gson.annotations.SerializedName;

public class AssetActivationStatusResponse {

    @SerializedName("data")
    private AssetActivationStatus data;

    public AssetActivationStatus getData() {
        return data;
    }
}