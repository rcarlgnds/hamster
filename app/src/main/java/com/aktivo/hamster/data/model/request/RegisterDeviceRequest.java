package com.aktivo.hamster.data.model.request;

import com.google.gson.annotations.SerializedName;

public class RegisterDeviceRequest {

    @SerializedName("token")
    private String token;

    @SerializedName("platform")
    private String platform;

    public RegisterDeviceRequest(String token, String platform) {
        this.token = token;
        this.platform = platform;
    }
}