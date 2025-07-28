package com.example.hamster.data.model.response;

import com.example.hamster.data.model.AssetCategory;
import java.util.List;

public class AssetCategoryResponse {
    private int page;
    private int limit;
    private int total;
    private List<AssetCategory> data;

    public AssetCategoryResponse(int page, int limit, int total, List<AssetCategory> data) {
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.data = data;
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
    public List<AssetCategory> getData() {
        return data;
    }
    public void setData(List<AssetCategory> data) {
        this.data = data;
    }
}

