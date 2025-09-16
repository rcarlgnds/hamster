package com.aktivo.hamster.data.model.request;

import com.google.gson.annotations.SerializedName;

public class UnregisterDeviceRequest {

    @SerializedName("token")
    private String token;

    public UnregisterDeviceRequest(String token) {
        this.token = token;
    }
}