package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Floor;
import java.util.List;

public class FloorResponse {
    private int page;
    private int limit;
    private int total;
    private List<Floor> data;

    public FloorResponse(int page, int limit, int total, List<Floor> data) {
        this.page = page;
        this.limit = limit;
        this.total = total;
        this.data = data;
    }

    // Getters & Setters
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
    public List<Floor> getData() {
        return data;
    }
    public void setData(List<Floor> data) {
        this.data = data;
    }
}
