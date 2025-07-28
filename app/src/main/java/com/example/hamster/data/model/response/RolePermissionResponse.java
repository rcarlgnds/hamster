package com.example.hamster.data.model.response;

import com.example.hamster.data.model.RolePermission;
import java.util.List;

public class RolePermissionResponse {
    private int page;
    private int limit;
    private int total;
    private List<RolePermission> data;

    public RolePermissionResponse(int page, int limit, int total, List<RolePermission> data) {
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
    public List<RolePermission> getData() {
        return data;
    }
    public void setData(List<RolePermission> data) {
        this.data = data;
    }
}

