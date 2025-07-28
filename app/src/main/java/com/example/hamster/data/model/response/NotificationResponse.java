package com.example.hamster.data.model.response;

import android.app.Notification;
import java.util.List;

public class NotificationResponse {
    private int page;
    private int limit;
    private int total;
    private List<Notification> data;

    public NotificationResponse(int page, int limit, int total, List<Notification> data) {
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
    public List<Notification> getData() {
        return data;
    }
    public void setData(List<Notification> data) {
        this.data = data;
    }
}

