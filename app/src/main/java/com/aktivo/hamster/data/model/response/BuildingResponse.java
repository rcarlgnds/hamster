package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.Building;
import com.aktivo.hamster.data.model.Pagination;

import java.io.Serializable;
import java.util.List;

public class BuildingResponse implements Serializable {
    private DataWrapper data;

    public DataWrapper getData() {
        return data;
    }

    public static class DataWrapper implements Serializable {
        private List<Building> data;
        private Pagination pagination;

        public List<Building> getData() {
            return data;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }
}