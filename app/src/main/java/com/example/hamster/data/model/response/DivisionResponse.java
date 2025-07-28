package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Division;
import java.util.List;

public class DivisionResponse {
    private int page;
    private int limit;
    private int total;
    private List<Division> data;

    public DivisionResponse(int page, int limit, int total, List<Division> data) {
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
    public List<Division> getData() {
        return data;
    }
    public void setData(List<Division> data) {
        this.data = data;
    }
}

