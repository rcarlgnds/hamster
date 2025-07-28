package com.example.hamster.data.model.response;

import com.example.hamster.data.model.AssetSubCategory;
import java.util.List;

public class AssetSubCategoryResponse {
    private int page;
    private int limit;
    private int total;
    private List<AssetSubCategory> data;

    public AssetSubCategoryResponse(int page, int limit, int total, List<AssetSubCategory> data) {
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
    public List<AssetSubCategory> getData() {
        return data;
    }
    public void setData(List<AssetSubCategory> data) {
        this.data = data;
    }
}

