package com.example.hamster.data.model.token;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequest {
    @SerializedName("refreshToken")
    private final String refreshToken;

    public RefreshTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}