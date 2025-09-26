package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class AssetsResponse implements Serializable {
    public boolean success;
    public String message;
    public DataWrapper data;
    public long timestamp;

    public static class DataWrapper {
        public List<Asset> data;
        public Pagination pagination;

        public List<Asset> getData() { return data; }
        public void setData(List<Asset> data) { this.data = data; }

        public Pagination getPagination() { return pagination; }
        public void setPagination(Pagination pagination) { this.pagination = pagination; }
    }

    public static class Pagination {
        public int currentPage;
        public int perPage;
        public int total;
        public int totalPages;
        public boolean hasNextPage;
        public boolean hasPrevPage;

        public int getCurrentPage() { return currentPage; }
        public int getPerPage() { return perPage; }
        public int getTotal() { return total; }
        public int getTotalPages() { return totalPages; }
        public boolean isHasNextPage() { return hasNextPage; }
        public boolean isHasPrevPage() { return hasPrevPage; }
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataWrapper getData() {
        return data;
    }

    public void setData(DataWrapper data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


}