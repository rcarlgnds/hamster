package com.example.hamster.data.model;

import java.io.Serializable;

public class AssetRejected implements Serializable {
    private String transactionId;
    private String assetCode;
    private String assetName;
    private String rejectedByPosition;
    private String status;

    public String getTransactionId() {
        return transactionId;
    }
    public String getAssetCode() {
        return assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getRejectedByPosition() {
        return rejectedByPosition;
    }

    public String getStatus() {
        return status;
    }
}
