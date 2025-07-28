// File: app/src/main/java/com/example/hamster/data/model/request/StartActivationRequest.java
package com.example.hamster.data.model.request;

public class StartActivationRequest {
    // Diubah dari assetId, initiatorId, notes
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