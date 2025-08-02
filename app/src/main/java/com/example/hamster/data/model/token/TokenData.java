package com.example.hamster.data.model.token;

import com.google.gson.annotations.SerializedName;

public class TokenData {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("refreshToken")
    private String refreshToken;


    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}