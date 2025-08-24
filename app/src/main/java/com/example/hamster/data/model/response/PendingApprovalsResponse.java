package com.example.hamster.data.model.response;

import java.util.List;

public class PendingApprovalsResponse {
    private int page;
    private int limit;
    private int total;
    private List<String> assetIds;

    public PendingApprovalsResponse(int page, int limit, int total, List<String> assetIds) {
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.assetIds = assetIds;
    }

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public List<String> getAssetIds() {
        return assetIds;
    }
    public void setAssetIds(List<String> assetIds) {
        this.assetIds = assetIds;
    }


}

