package com.example.hamster.data.model;

import com.google.gson.annotations.SerializedName;

public class ApprovalItem {

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("assetId")
    private String assetId;

    @SerializedName("assetCode")
    private String assetCode;

    @SerializedName("assetName")
    private String assetName;

    @SerializedName("room")
    private String room;

    @SerializedName("createdAt")
    private long createdAt;

    public String getTransactionId() { return transactionId; }
    public String getAssetId() { return assetId; }
    public String getAssetCode() { return assetCode; }
    public String getAssetName() { return assetName; }
    public String getRoom() { return room; }
    public long getCreatedAt() { return createdAt; }
}