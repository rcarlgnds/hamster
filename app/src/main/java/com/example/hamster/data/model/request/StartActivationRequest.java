package com.example.hamster.data.model.request;

public class StartActivationRequest {
    private String assetCode;

    public StartActivationRequest(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }
}