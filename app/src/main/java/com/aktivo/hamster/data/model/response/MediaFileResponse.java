package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.MediaFile;
import java.util.List;

public class MediaFileResponse {
    private int page;
    private int limit;
    private int total;
    private List<MediaFile> data;

    public MediaFileResponse(int page, int limit, int total, List<MediaFile> data) {
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
    public List<MediaFile> getData() {
        return data;
    }
    public void setData(List<MediaFile> data) {
        this.data = data;
    }
}

