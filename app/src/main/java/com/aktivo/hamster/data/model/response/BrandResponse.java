package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.Brand;
import java.util.List;

public class BrandResponse {
    private int page;
    private int limit;
    private int total;
    private List<Brand> data;

    public BrandResponse(int page, int limit, int total, List<Brand> data) {
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
    public List<Brand> getData() {
        return data;
    }
    public void setData(List<Brand> data) {
        this.data = data;
    }
}

