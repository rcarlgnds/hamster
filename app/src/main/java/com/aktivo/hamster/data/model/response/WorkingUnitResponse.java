package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.WorkingUnit;
import java.util.List;

public class WorkingUnitResponse {
    private int page;
    private int limit;
    private int total;
    private List<WorkingUnit> data;

    public WorkingUnitResponse(int page, int limit, int total, List<WorkingUnit> data) {
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
    public List<WorkingUnit> getData() {
        return data;
    }
    public void setData(List<WorkingUnit> data) {
        this.data = data;
    }
}

