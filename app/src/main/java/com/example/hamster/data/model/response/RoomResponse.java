package com.example.hamster.data.model.response;

import com.example.hamster.data.model.Room;
import java.util.List;

public class RoomResponse {
    private int page;
    private int limit;
    private int total;
    private List<Room> data;

    public RoomResponse(int page, int limit, int total, List<Room> data) {
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
    public List<Room> getData() {
        return data;
    }
    public void setData(List<Room> data) {
        this.data = data;
    }
}
