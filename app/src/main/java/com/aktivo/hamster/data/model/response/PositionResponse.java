package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.Position;
import java.util.List;

public class PositionResponse {
    private int page;
    private int limit;
    private int total;
    private List<Position> data;

    public PositionResponse(int page, int limit, int total, List<Position> data) {
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
    public List<Position> getData() {
        return data;
    }
    public void setData(List<Position> data) {
        this.data = data;
    }
}

