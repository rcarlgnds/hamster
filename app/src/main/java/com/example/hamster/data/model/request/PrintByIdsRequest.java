package com.example.hamster.data.model.request;

import java.util.List;

public class PrintByIdsRequest {
    private List<String> assetIds;

    public PrintByIdsRequest(List<String> assetIds) {
        this.assetIds = assetIds;
    }

    public List<String> getAssetIds() {
        return assetIds;
    }
    public void setAssetIds(List<String> assetIds) {
        this.assetIds = assetIds;
    }
}

