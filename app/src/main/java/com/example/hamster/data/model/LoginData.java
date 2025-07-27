package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("accessToken")
    private String accessToken;
    @SerializedName("refreshToken")
    private String refreshToken;
    @SerializedName("tokenType")
    private String tokenType;
    @SerializedName("expiresIn")
    private int expiresIn;
    @SerializedName("expiresAt")
    private long expiresAt;
    @SerializedName("user")
    private User user;

    // Getters
    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public int getExpiresIn() { return expiresIn; }
    public long getExpiresAt() { return expiresAt; }
    public User getUser() { return user; }
}