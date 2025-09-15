package com.aktivo.hamster.data.model.token;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenResponse {
    @SerializedName("data")
    private TokenData data;

    public TokenData getData() {
        return data;
    }
}