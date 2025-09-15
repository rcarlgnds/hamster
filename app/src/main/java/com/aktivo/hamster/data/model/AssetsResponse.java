package com.aktivo.hamster.data.model;

import java.io.Serializable;
import java.util.List;

public class AssetsResponse implements Serializable {
    private DataWrapper data;

    public DataWrapper getData() {
        return data;
    }

    public static class DataWrapper implements Serializable {
        private List<Asset> data;
        private Pagination pagination;

        public List<Asset> getData() {
            return data;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }
}