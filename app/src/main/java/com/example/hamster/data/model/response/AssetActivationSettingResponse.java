package com.example.hamster.data.model.response;

import com.example.hamster.data.model.AssetActivationSetting;
import java.util.List;

public class AssetActivationSettingResponse {
    private int page;
    private int limit;
    private int total;
    private List<AssetActivationSetting> data;

    public AssetActivationSettingResponse(int page, int limit, int total, List<AssetActivationSetting> data) {
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
    public List<AssetActivationSetting> getData() {
        return data;
    }
    public void setData(List<AssetActivationSetting> data) {
        this.data = data;
    }
}

