package com.example.hamster.data.model.token;

import com.google.gson.annotations.SerializedName;

public class TokenData {
    @SerializedName("accessToken")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }
}