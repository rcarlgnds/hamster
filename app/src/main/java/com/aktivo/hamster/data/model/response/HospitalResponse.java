package com.aktivo.hamster.data.model.response;

import com.aktivo.hamster.data.model.Hospital;
import com.aktivo.hamster.data.model.Pagination;

import java.io.Serializable;
import java.util.List;

public class HospitalResponse implements Serializable {
    private DataWrapper data;

    public DataWrapper getData() {
        return data;
    }

    public static class DataWrapper implements Serializable {
        private List<Hospital> data;
        private Pagination pagination;

        public List<Hospital> getData() {
            return data;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }
}